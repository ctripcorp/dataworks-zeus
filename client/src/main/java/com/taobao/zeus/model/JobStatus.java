package com.taobao.zeus.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Job的状态
 * 用于持久化Job状态，重启服务器时用作恢复
 * @author zhoufang
 *
 */
public class JobStatus implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String jobId;
	
	private Status status;
	
	private String historyId;
	/**
	 * 依赖的Job的状态
	 * key 依赖的jobId
	 * value 依赖的Job的完成时间
	 */
	private Map<String, String> readyDependency=new HashMap<String, String>();
	
	
	public String getJobId(){
		return jobId;
	}
	public void setJobId(String jobId){
		this.jobId=jobId;
	}
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
	
	/**
	 * 触发任务执行的3种类型
	 * 1： 定时执行
	 * 2：手动执行(不产生连锁反应)
	 * 3：手动恢复(产生连锁反应)
	 * @author zhoufang
	 *
	 */
	public enum TriggerType{
		SCHEDULE(1),MANUAL(2),MANUAL_RECOVER(3);
		private Integer id;
		private TriggerType(Integer id){
			this.id=id;
		}
		@Override
		public String toString() {
			return id.toString();
		}
		public String toName(){
			if(id==1){
				return "自动调度";
			}else if(id==2){
				return "手动触发";
			}else if(id==3){
				return "手动恢复";
			}
			return "未知";
		}
		public static TriggerType parser(Integer v){
			for(TriggerType type:TriggerType.values()){
				if(type.getId().equals(v)){
					return type;
				}
			}
			return null;
		}
		public Integer getId() {
			return id;
		}
	}
	public Status getStatus(){
		return status;
	}
	public void setStatus(Status status){
		this.status=status;
	}
	public Map<String, String> getReadyDependency() {
		return readyDependency;
	}
	public void setReadyDependency(Map<String, String> readyDependency){
		this.readyDependency=readyDependency;
	}
	public String getHistoryId() {
		return historyId;
	}
	public void setHistoryId(String historyId) {
		this.historyId = historyId;
	}
}
