package com.taobao.zeus.socket.master;

public class JobElement {
	private String jobID;
	private String host;
	private int priorityLevel;

	/**
	 * @return
	 */
	public JobElement() {

	}

	public JobElement(String jobID, String host) {
		this.jobID = jobID;
		this.host = host;
	}
	
	public JobElement(String jobID, String host, int priorityLevel) {
		this.jobID = jobID;
		this.host = host;
		this.priorityLevel = priorityLevel;
	}

	public String getJobID() {
		return jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(int priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	@Override
	public String toString() {
		return jobID + ":" + host;
	}
}
