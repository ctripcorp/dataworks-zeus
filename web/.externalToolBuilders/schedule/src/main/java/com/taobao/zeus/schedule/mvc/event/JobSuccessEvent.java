package com.taobao.zeus.schedule.mvc.event;

import com.taobao.zeus.model.JobStatus.TriggerType;
import com.taobao.zeus.mvc.AppEvent;
/**
 * Job执行成功事件
 * @author zhoufang
 *
 */
public class JobSuccessEvent extends AppEvent{
	private String historyId;
	private String jobId;
	private TriggerType triggerType;
	private String statisEndTime;
	public JobSuccessEvent(String jobId,TriggerType triggerType,String historyId) {
		super(Events.JobSucceed);
		this.jobId=jobId;
		this.triggerType=triggerType;
		this.historyId=historyId;
	}	

	public String getJobId() {
		return jobId;
	}

	public String getHistoryId() {
		return historyId;
	}

	public TriggerType getTriggerType() {
		return triggerType;
	}



	public String getStatisEndTime() {
		return statisEndTime;
	}



	public void setStatisEndTime(String statisEndTime) {
		this.statisEndTime = statisEndTime;
	}


}
