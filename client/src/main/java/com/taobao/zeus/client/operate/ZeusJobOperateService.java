package com.taobao.zeus.client.operate;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.JobStatus.TriggerType;
/**
 * 对Zeus Job的操作处理
 * @author zhoufang
 *
 */
public interface ZeusJobOperateService {
	/**
	 * 设置任务的自动调度开关
	 * @param uid
	 * @param jobId
	 * @param auto
	 */
	public void autoScheduleJob(String uid,String jobId,Boolean auto) throws ZeusException;
	/**
	 * 执行一个任务
	 * 触发类型只能设置  手动触发  或者  手动恢复
	 * @param uid
	 * @param jobId
	 * @param type
	 * @throws ZeusException
	 */
	@Deprecated
	public void executeJob(String uid,String jobId,TriggerType type) throws ZeusException;
	
	/**
	 * 手动执行任务，并且返回该任务的historyId
	 * @param uid
	 * @param jobId
	 * @return historyId
	 * @throws ZeusException
	 */
	@Deprecated
	public String executeJob(String uid,String jobId) throws ZeusException;
}
