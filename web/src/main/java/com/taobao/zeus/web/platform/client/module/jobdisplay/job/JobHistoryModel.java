package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import java.io.Serializable;
import java.util.Date;

public class JobHistoryModel implements Serializable{
	private static final long serialVersionUID = 1L;

	private String id;
	private String jobId;
	private String toJobId;
	private String name;
	private String owner;
	private Date startTime;
	private Date endTime;
	private String executeHost;
	private String status;
	private String triggerType;
	private String illustrate;
	private String operator;
	private String log;
	private String statisEndTime;
	private String timeZone;
	private String cycle;
	private String host;
	
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
	public String getToJobId() {
		return toJobId;
	}
	public void setToJobId(String toJobId) {
		this.toJobId = toJobId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public String getExecuteHost() {
		return executeHost;
	}
	public void setExecuteHost(String executeHost) {
		this.executeHost = executeHost;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}
	public String getIllustrate() {
		return illustrate;
	}
	public void setIllustrate(String illustrate) {
		this.illustrate = illustrate;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getStatisEndTime() {
		return statisEndTime;
	}
	public void setStatisEndTime(String statisEndTime) {
		this.statisEndTime = statisEndTime;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getCycle() {
		return cycle;
	}
	public void setCycle(String cycle) {
		this.cycle = cycle;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
}
