package com.taobao.zeus.schedule.mvc.event;

import com.taobao.zeus.mvc.AppEvent;

public class JobCancelEvent extends AppEvent{

	private String jobId;
	public JobCancelEvent(String jobId) {
		super(Events.JobCancel);
		this.jobId=jobId;
	}

}
