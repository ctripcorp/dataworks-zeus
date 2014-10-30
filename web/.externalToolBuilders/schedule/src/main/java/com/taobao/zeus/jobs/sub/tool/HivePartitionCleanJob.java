package com.taobao.zeus.jobs.sub.tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.conf.ConfUtil;
import com.taobao.zeus.model.processer.HiveProcesser;
import com.taobao.zeus.store.CliTableManager;
import com.taobao.zeus.store.TableManager;

public class HivePartitionCleanJob extends AbstractJob {
	@SuppressWarnings("unused")
	private HiveProcesser processer;
	private List<String> tables;
	private Integer keepDays;
	private TableManager tableManager;

	public HivePartitionCleanJob(final JobContext jobContext,
			final HiveProcesser p, final ApplicationContext applicationContext)
			throws Exception {
		super(jobContext);
		this.processer = p;
		this.tables = p.getOutputTables();
		this.keepDays = p.getKeepDays();
		this.tableManager = new CliTableManager(ConfUtil.getDefaultCoreSite());
	}

	@Override
	public Integer run() throws Exception {
		if (jobContext.getCoreExitCode() != 0) {
			log("Job 运行失败，不进行产出目录清理");
			return 0;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, keepDays * (-1));
		Date limit = format.parse(format.format(cal.getTime()));
		for (String tableName : tables) {
			Table t = tableManager.getTable(tableName);
			int ptIndex = -1;
			for (FieldSchema fs : t.getPartitionKeys()) {
				ptIndex++;
				if (fs.getName().equalsIgnoreCase("pt")) {
					break;
				}
			}
			if (ptIndex < 0) {
				log("表" + tableName + "不含pt分区字段，不进行历史分区清理");
				continue;
			}
			List<Partition> parts = tableManager.getPartitions(tableName, null);
			for (Partition p : parts) {
				Date ptDate = null;
				try {
					ptDate = format.parse(StringUtils.substring(p.getValues()
							.get(ptIndex), 0, 8));
				} catch (Exception e) {
					log("分区字段格式非法：");
					log(e);
				}
				if (ptDate == null) {
					log("解析分区时间失败。" + p.getValues().get(ptIndex));
					continue;
				}
				if (ptDate.before(limit)) {
					if (!tableManager.dropPartition(tableName, p.getValues(),
							true)) {
						log("drop partition failed.table[" + tableName
								+ "],part_vals=[" + p.getValues());
					}else{
						log("drop partition ok. Table[" + tableName
								+ "],part_vals=[" + p.getValues()+"]");
					}
				}

			}
		}
		return 0;
	}

	@Override
	public void cancel() {
		canceled = true;
	}

}
