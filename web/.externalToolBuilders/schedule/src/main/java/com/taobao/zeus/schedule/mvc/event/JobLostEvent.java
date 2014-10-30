package com.taobao.zeus.schedule.mvc.event;

import com.taobao.zeus.mvc.AppEvent;
import com.taobao.zeus.mvc.EventType;

public class JobLostEvent extends AppEvent {
	
	private final String jobId;
	public JobLostEvent(EventType type,String jobId){
		super(type);
		this.jobId=jobId;
	}
	public String getJobId() {
		return jobId;
	}

}
