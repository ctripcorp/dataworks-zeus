package com.taobao.zeus.jobs.sub.tool;

import java.util.ArrayList;
import java.util.List;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.ProcessJob;
import com.taobao.zeus.util.JobUtils;

/**
 * 扫描日志，揪出hadoop任务id，进行kill
 * 
 * @author zhoufang
 * 
 */
public class CancelHadoopJob extends ProcessJob {

	public CancelHadoopJob(JobContext jobContext) {
		super(jobContext);
	}

	@Override
	public void cancel() {
		canceled = true;
	}

	@Override
	public List<String> getCommandList() {
		List<String> list = new ArrayList<String>();
		// 检测日志，如果有hadoop的job，先kill 这些job
		String log = null;
		if (jobContext.getJobHistory() != null) {
			log = jobContext.getJobHistory().getLog().getContent();
		} else if (jobContext.getDebugHistory() != null) {
			log = jobContext.getDebugHistory().getLog().getContent();
		}
		if (log != null) {
			String hadoopCmd=JobUtils.getHadoopCmd(envMap);
			String[] logs = log.split("\n");
			for (String g : logs) {
				String cmd = null;
				if (g.contains("Running job: ")) {// mapreduce
					String jobId = g.substring(g.indexOf("job_"));
					cmd = hadoopCmd+" job -kill " + jobId;
				} else if (g.contains("Starting Job =")) {// hive
					String jobId = g.substring(g.lastIndexOf("job_"));
					cmd = hadoopCmd+" job -kill " + jobId;
				}
				if (cmd != null) {
					list.add(cmd);
				}
			}
		}
		return list;
	}
}
