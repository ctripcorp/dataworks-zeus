package com.taobao.zeus.store;

import java.util.List;
import java.util.Map;

import com.taobao.zeus.model.JobHistory;

public interface JobHistoryManager {

	public JobHistory addJobHistory(JobHistory history);
	
	JobHistory findJobHistory(String id);
	/**
	 * 分页查询Job历史记录，注意返回结果中不包含 日志内容 字段
	 * @param jobId
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<JobHistory> pagingList(final String jobId,final int start,final int limit);
	public int pagingTotal(String jobId);
	
	public void updateJobHistoryLog(String id,String log);
	/**
	 * 更新JobLogHistory，但是不保护log字段
	 * @param history
	 */
	public void updateJobHistory(JobHistory history);
	/**
	 * 批量获取Job的最后一次执行历史
	 * @param historyIdList
	 * @return
	 */
	public Map<String,JobHistory> findLastHistoryByList(List<String> jobIds);
	/**
	 * 获取最近24小时内，运行状态的所有记录
	 * @return
	 */
	public List<JobHistory> findRecentRunningHistory();
}
