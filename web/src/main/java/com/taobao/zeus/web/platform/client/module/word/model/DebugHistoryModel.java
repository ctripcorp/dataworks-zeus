package com.taobao.zeus.web.platform.client.module.word.model;

import java.io.Serializable;
import java.util.Date;

public class DebugHistoryModel implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	private String fileId;
	private Date startTime;
	private Date endTime;
	private String executeHost;
	private Status status;
	private JobRunType jobRunType;
	private Date gmtCreate=new Date();
	private Date gmtModified=new Date();
	private String script="";
	private String log="";
	
	/**
	 * WAIT:
	 * 	Job没有开始，或者Job依赖的任务没有全部完成
	 * RUNNING
	 * 	Job正在运行中
	 * SUCCESS
	 * 	Job运行成功(瞬间状态)
	 * FAILED
	 * 	Job运行失败(瞬间状态)
	 * @author zhoufang
	 *
	 */
	public enum Status{
		WAIT("wait"),RUNNING("running"),SUCCESS("success"),FAILED("failed");
		
		private final String id;
		private Status(String id){
			this.id=id;
		}
		@Override
		public String toString() {
			return id;
		}
		public static Status parser(String v){
			for(Status s:Status.values()){
				if(s.id.equalsIgnoreCase(v)){
					return s;
				}
			}
			return null;
		}
		public String getId() {
			return id;
		}
	}
	
	public enum JobRunType{
		MapReduce("main"),Shell("shell"),Hive("hive");
		private final String id;
		JobRunType(String s){
			this.id=s;
		}
		@Override
		public String toString() {
			return id;
		}
		public static JobRunType parser(String v){
			for(JobRunType type:JobRunType.values()){
				if(type.toString().equals(v)){
					return type;
				}
			}
			return null;
		}
	}
	
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		log=log==null?"":log;
		this.log = log;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
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
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public JobRunType getJobRunType() {
		return jobRunType;
	}
	public void setJobRunType(JobRunType jobRunType) {
		this.jobRunType = jobRunType;
	}
}
