package com.taobao.zeus.socket.master;

public class JobElement {
	private String jobID;
	private String host;

	/**
	 * @return
	 */
	public JobElement() {

	}

	public JobElement(String jobID, String host) {
		this.jobID = jobID;
		this.host = host;
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

	@Override
	public String toString() {
		return jobID + ":" + host;
	}
}
