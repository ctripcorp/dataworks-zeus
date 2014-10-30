package com.taobao.zeus.jobs.sub.tool;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.conf.ConfUtil;
import com.taobao.zeus.model.processer.OutputCheckProcesser;

public class OutputCheckJob extends AbstractJob {

	private OutputCheckProcesser ocp;
	private String path;

	public OutputCheckJob(JobContext jobContext, OutputCheckProcesser p,
			ApplicationContext applicationContext) {
		super(jobContext);
		this.ocp = p;

		path = ocp.getPath();
	}

	@Override
	public Integer run() throws Exception {
		if (jobContext.getCoreExitCode() != 0) {
			jobContext.getJobHistory().getLog()
					.appendZeus("Job 运行失败，不进行产出数据大小检测");
			return 0;
		}
		jobContext.getJobHistory().getLog()
				.appendZeus("OutputCheck 开始进行产出数据大小检测");
		String upperPath = path;
		if (upperPath.endsWith("/")) {
			upperPath = upperPath.substring(0, path.length() - 1);
		}
		upperPath = upperPath.substring(0, upperPath.lastIndexOf("/"));
		Path hdfsPath = new Path(upperPath);
		FileSystem fs = FileSystem.get(ConfUtil.getDefaultCoreSite());
		FileStatus[] files = fs.listStatus(hdfsPath);
		double total = 0;
		List<ContentSummary> dirFiles = new ArrayList<ContentSummary>();
		for (FileStatus f : files) {
			if (f.isDir()) {
				ContentSummary cs = fs.getContentSummary(f.getPath());
				if (cs.getLength() > 0) {
					dirFiles.add(cs);
					total += cs.getLength();
				}
			}
		}
		double ava = total / dirFiles.size();
		double upper = ava * 1.5;
		double lower = ava * 0.5;
		List<ContentSummary> valid = new ArrayList<ContentSummary>();
		for (ContentSummary cs : dirFiles) {
			if (cs.getLength() < upper && cs.getLength() > lower) {
				valid.add(cs);
			}
		}
		total = 0d;
		for (ContentSummary cs : valid) {
			total += cs.getLength();
		}
		ava = total / valid.size();

		jobContext.getJobHistory().getLog().appendZeus("产出数据上层路径：" + upperPath);
		jobContext.getJobHistory().getLog()
				.appendZeus("有效的参考文件夹个数：" + valid.size());
		jobContext.getJobHistory().getLog().appendZeus("平均产出数据大小：" + ava);

		jobContext.getJobHistory().getLog()
				.appendZeus("设定数据大小浮动百分比：" + ocp.getPercent() + "%");
		jobContext.getJobHistory().getLog().appendZeus("当前任务产出数据路径：" + path);

		ContentSummary current = null;
		try {
			current = fs.getContentSummary(new Path(path));
		} catch (Exception e) {
			log("本次job产出数据的文件夹有问题");
			log(e);
		}
		if (current != null) {
			jobContext.getJobHistory().getLog()
					.appendZeus("本次job产出数据大小：" + current.getLength());
		} else {
			return -1;
		}

		if ((Math.abs(current.getLength() - ava) / ava) > (ocp.getPercent() / 100.0)) {
			double rate = Math.abs(current.getLength() - ava) / ava;
			if (rate > (ocp.getPercent() / 100.0)) {
				// 超出浮动范围
				jobContext.getJobHistory().getLog().appendZeus("超出设定浮动比例，发出报警");
				String jobId = jobContext.getJobHistory().getJobId();
				StringBuffer sb = new StringBuffer("jobid=" + jobId
						+ " 产出数据大小超出浮动比例 " + ocp.getPercent() + "%");
				sb.append("\n平均产出数据大小为：" + ava);
				sb.append("\n本次产出数据大小为：" + current.getLength());
			}
		} else {
			jobContext.getJobHistory().getLog().appendZeus("产出数据检测OK");
		}
		return 0;
	}

	@Override
	public void cancel() {
		canceled = true;
	}

}
