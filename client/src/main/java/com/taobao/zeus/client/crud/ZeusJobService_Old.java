package com.taobao.zeus.client.crud;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobDescriptor.JobRunType;

public interface ZeusJobService_Old {

	public JobDescriptor createJob(String uid,String jobName,String parentGroup,JobRunType jobType) throws ZeusException;
	
	public void updateJob(String uid,JobDescriptor jobDescriptor) throws ZeusException;
	
	public void deleteJob(String uid,String jobId) throws ZeusException;
	
	public JobDescriptor getJobDescriptor(String jobId);
	
}
