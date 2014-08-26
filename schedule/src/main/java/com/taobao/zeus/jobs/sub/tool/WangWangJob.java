package com.taobao.zeus.jobs.sub.tool;

import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.JobContext;

public class WangWangJob extends AbstractJob{

	public WangWangJob(JobContext jobContext) {
		super(jobContext);
	}

	@Override
	public Integer run() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel() {
		canceled=true;
	}

}
