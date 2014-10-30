package com.taobao.zeus.jobs.sub.tool;

import java.util.Arrays;
import java.util.List;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.ProcessJob;

public class CopyLocalFileJob extends ProcessJob{

	private String sourcePath;
	public CopyLocalFileJob(JobContext jobContext,String path) {
		super(jobContext);
		this.sourcePath=path;
	}

	@Override
	public List<String> getCommandList() {
		String command="cp "+sourcePath+" "+getJobContext().getWorkDir();
		return Arrays.asList(command);
	}

}
