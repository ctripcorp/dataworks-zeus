package com.taobao.zeus.jobs.sub;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.util.RunningJobKeys;

public class HadoopShellJob extends ShellJob{

	public HadoopShellJob(JobContext jobContext) {
		super(jobContext);
		jobContext.getProperties().setProperty(RunningJobKeys.JOB_RUN_TYPE, "HadoopShellJob");
	}
	
	@Override
	public Integer run() throws Exception {
		return super.run();
	}
}
