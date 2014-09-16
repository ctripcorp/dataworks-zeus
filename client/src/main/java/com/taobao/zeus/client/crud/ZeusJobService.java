package com.taobao.zeus.client.crud;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.JobDescriptorOld;
import com.taobao.zeus.model.JobDescriptorOld.JobRunTypeOld;

public interface ZeusJobService {

	public JobDescriptorOld createJob(String uid,String jobName,String parentGroup,JobRunTypeOld jobType) throws ZeusException;
	
	public void updateJob(String uid,JobDescriptorOld jobDescriptor) throws ZeusException;
	
	public void deleteJob(String uid,String jobId) throws ZeusException;
	
	public JobDescriptorOld getJobDescriptor(String jobId);
	
}
