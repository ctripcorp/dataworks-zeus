package com.taobao.zeus.jobs.sub.tool;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.broadcast.alarm.ZeusAlarm;
import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.conf.ConfUtil;
import com.taobao.zeus.model.processer.HiveProcesser;
import com.taobao.zeus.store.CliTableManager;
import com.taobao.zeus.store.HDFSManager;
import com.taobao.zeus.store.TableManager;
import com.taobao.zeus.util.ZeusDateTool;

public class HiveOutputCheckJob extends AbstractJob {

	@SuppressWarnings("unused")
	private HiveProcesser processer;
	private List<String> tableNames;
	private Integer percent;
	private ZeusAlarm wangWangAlarm;
	Integer exitCode = 0;
	private Configuration conf;

	private TableManager tableManager;

	public HiveOutputCheckJob(final JobContext jobContext,
			final HiveProcesser p, final ApplicationContext applicationContext)
			throws Exception {
		super(jobContext);
		this.wangWangAlarm = (ZeusAlarm) applicationContext
				.getBean("wangWangAlarm");
		conf = ConfUtil.getDefaultCoreSite();
		this.tableManager = new CliTableManager(conf);
		this.processer = p;
		this.tableNames = p.getOutputTables();
		this.percent = p.getDriftPercent();
	}

	@Override
	public Integer run() throws Exception {

		if (jobContext.getCoreExitCode() != 0) {
			jobContext.getJobHistory().getLog()
					.appendZeus("Job 运行失败，不进行产出分区大小检测");
			return exitCode;
		}
		jobContext.getJobHistory().getLog()
				.appendZeus("HiveOutputCheck 开始进行产出分区大小检测");
		for (String tableName : this.tableNames) {

			log("开始进行检测表：" + tableName);
			//FIXME hive表
			Table t = tableManager.getTable("default",tableName);
			// 表不存在
			if (t == null) {
				tableFailed("表 " + tableName + " 不存在。");
				continue;
			}
			//FIXME hive
			List<Partition> parts = tableManager.getPartitions("default", tableName, null);
			// 没有分区
			if (parts == null || parts.isEmpty()) {
				tableFailed("表 " + tableName + " 没有获取到存在的分区。");
				continue;
			}

			/********************
			 * 存在分区 ，继续检查
			 */
			int ptIndex = getPtIndex(t);
			if (ptIndex != 0) {
				log("表" + tableName + "的第一个分区字段不是pt。只支持第一个分区字段为pt的表。");
				exitCode = -1;
				continue;
			}
			String lastDay = new ZeusDateTool(new Date()).addDay(-1).format("yyyyMMdd");

			// 获取最新分区
			Partition lastPartition = getLastPartition(parts, lastDay, ptIndex);
			if (lastPartition == null) {
				tableFailed("表" + tableName + "最新分区pt=" + lastDay + "不存在");
				continue;
			}

			// 可能有多级分区，按pt把分区大小聚合
			HashMap<String, Long> sizeMap = getSizeMap(parts, ptIndex);

			// 检查最新分区
			Long lastSize = sizeMap.get(lastDay);
			if (lastSize == null || lastSize <= 0) {
				tableFailed("表" + tableName + "最新分区pt=" + lastDay
						+ "目录不存在或大小为零。");
				continue;
			}

			// 只有一个分区，不用继续检查
			if (sizeMap.keySet().size() == 1) {
				log("表" + tableName + "只有最新分区pt=" + lastDay + "。分区大小:"+lastSize+".不用检测浮动。");
				continue;
			}

			// 有多个分区，检查浮动
			// 去除最新分区
			sizeMap.remove(lastDay);

			// 去除不正常数据
			long avgSize = getAvgSize(sizeMap);

			// 正常数据范围
			long upLimit = (long) (1.5 * avgSize);
			long downLimit = (long) (0.5 * avgSize);

			// 去除不正常数据,重新计算平均大小
			for (Iterator<Entry<String, Long>> itr = sizeMap.entrySet()
					.iterator(); itr.hasNext();) {
				Entry<String, Long> entry = itr.next();
				if (entry.getValue() == null || entry.getValue() < downLimit
						|| entry.getValue() > upLimit) {
					itr.remove();
				}
			}
			if (sizeMap.size() != 0) {
				avgSize = getAvgSize(sizeMap);
			} else {
				avgSize = 0;
			}

			// 浮动检查
			log(" 表【" + tableName + "】 ");
			log("有效的参考分区个数：" + sizeMap.size());
			log("平均分区大小：" + avgSize);

			log("设定分区大小浮动百分比：" + this.percent + "%");
			log("最新分区大小：" + lastSize);
			if (avgSize > 0) {
				log("最新分区浮动百分比：" + (lastSize - avgSize) * 100 / (avgSize) + "%");
			}
			upLimit = (long) (avgSize * ((100.0 + this.percent) / 100));
			downLimit = (long) (avgSize * ((100.0 - this.percent) / 100));
			if (lastSize >= downLimit && lastSize <= upLimit) {
				log(" 表【" + tableName + "】 " + "产出数据检测OK");
			} else {
				// 超出浮动范围
				String jobId = jobContext.getJobHistory().getJobId();
				StringBuffer sb = new StringBuffer("jobid=" + jobId + " 表【"
						+ tableName + "】 " + " 产出分区大小超出浮动比例 " + this.percent
						+ "%");
				sb.append("\n平均分区大小为：" + avgSize);
				sb.append("\n本次产出分区大小为：" + lastSize + "\n");
				tableFailed(sb.toString());
			}
		}
		return exitCode;
	}

	private long getAvgSize(final HashMap<String, Long> sizeMap) {
		long avgTotal = 0;
		for (Long size : sizeMap.values()) {
			if (size != null) {
				avgTotal += size / sizeMap.size();
			}
		}
		return avgTotal;
	}

	private HashMap<String, Long> getSizeMap(final List<Partition> parts,
			final int ptIndex) {
		HashMap<String, Long> sizeMap = new HashMap<String, Long>(parts.size());
		for (Partition p : parts) {
			long size = HDFSManager.getPathSize(p.getSd().getLocation(), conf);
			String pt = StringUtils.substring(p.getValues().get(ptIndex), 0, 8);
			Long tempSize = sizeMap.get(pt);
			if (tempSize == null || tempSize < 0) {
				tempSize = 0L;
			}
			sizeMap.put(pt, tempSize + size);
		}
		return sizeMap;
	}

	@Override
	public void cancel() {
		canceled = true;
	}

	private void tableFailed(final String msg) throws Exception {
		exitCode = -1;
		log(msg + " 发出报警");
		wangWangAlarm.alarm(jobContext.getJobHistory().getId(),
				"Zeus任务产出分区检查报警", msg);
	}

	private int getPtIndex(final Table t) {
		int ptIndex = 0;
		boolean hasPt = false;
		for (FieldSchema fs : t.getPartitionKeys()) {
			if (fs.getName().equals("pt")) {
				hasPt = true;
				break;
			}
			ptIndex++;
		}
		if (!hasPt) {
			return -1;
		}
		return ptIndex;
	}

	private Partition getLastPartition(final Collection<Partition> parts,
			final String lastDay, final int ptIndex) {
		for (Partition p : parts) {
			if (StringUtils.substring(p.getValues().get(ptIndex), 0, 8).equals(
					lastDay)) {
				return p;
			}
		}
		return null;
	}

}
