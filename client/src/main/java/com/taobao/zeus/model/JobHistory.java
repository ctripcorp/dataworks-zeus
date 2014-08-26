package com.taobao.zeus.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.taobao.zeus.model.JobStatus.Status;
import com.taobao.zeus.model.JobStatus.TriggerType;


public class JobHistory{

	private String id;
	private String jobId;
	private Date startTime;
	private Date endTime;
	private String executeHost;
	private String operator;
	private Status status;
	private TriggerType triggerType;
	private String illustrate;
	private String statisEndTime;
	private LogContent log=new LogContent();
	private String timezone;
	private String cycle;

	private Map<String, String> properties=new HashMap<String, String>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public LogContent getLog() {
		return log;
	}
	public void setLog(String log) {
		log=log==null?"":log;
		this.log.setContent(new StringBuffer(log));
	}

	public String getExecuteHost() {
		return executeHost;
	}

	public void setExecuteHost(String executeHost) {
		this.executeHost = executeHost;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public TriggerType getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(TriggerType triggerType) {
		this.triggerType = triggerType;
	}

	public String getIllustrate() {
		return illustrate;
	}

	public void setIllustrate(String illustrate) {
		this.illustrate = illustrate;
	}
	
	@Override
	public String toString() {
		return "id:"+id+",jobId:"+jobId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getStatisEndTime() {
		return statisEndTime;
	}

	public void setStatisEndTime(String statisEndTime) {
		this.statisEndTime = statisEndTime;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
}
