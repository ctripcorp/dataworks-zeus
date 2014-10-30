package com.taobao.zeus.web.platform.shared.rpc;

import java.io.IOException;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobHistoryModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModelAction;
import com.taobao.zeus.web.platform.client.util.GwtException;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

/**
 *
 * @author zhoufang
 */
@RemoteServiceRelativePath("job.rpc")
public interface JobService extends RemoteService {
	/**
	 * 创建一个Job任务
	 * @param jobData
	 * @throws ServiceException
	 * @throws IOException
	 */
	JobModel createJob(String jobName,String parentGroupId,String jobType) throws GwtException;
	
	JobModel getUpstreamJob(String jobId) throws GwtException;
	
	JobModel updateJob(JobModel jobModel) throws GwtException;
	/**
	 * 开关
	 * @param auto
	 * @throws GwtException
	 */
	Boolean switchAuto(String jobId,Boolean auto) throws GwtException;
	/**
	 * 运行程序
	 * 1：手动运行
	 * 2:手动恢复
	 * @param jobId
	 * @param type
	 * @throws GwtException
	 */
	void run(String jobId,int type) throws GwtException;
	/**
	 * 取消一个正在执行的任务
	 * @param historyId
	 * @throws GwtException
	 */
	void cancel(String historyId) throws GwtException;
	/**
	 * 分页查询Job任务的历史日志
	 * @param config
	 * @return
	 */
	PagingLoadResult<JobHistoryModel> jobHistoryPaging(String jobId,PagingLoadConfig config);
	/**
	 * 获取Job任务的详细日志
	 * @param id
	 * @return
	 */
	JobHistoryModel getJobHistory(String id);
	/**
	 * 获取Job的运行状态
	 * @param jobId
	 * @return
	 */
	JobModel getJobStatus(String jobId);
	/**
	 * 获取组下的所有任务任务状态
	 * @param groupId
	 * @return
	 */
	PagingLoadResult<JobModelAction> getSubJobStatus(String groupId,PagingLoadConfig config);

	/**
	 * 获取组下正在运行的自动job
	 * @param groupId
	 * @return
	 */
//	@Deprecated
//	List<JobHistoryModel> getRunningJobs(String groupId);
	
	List<JobHistoryModel> getAutoRunning(String groupId);
	/**
	 * 获取正在运行的手动任务
	 * @param groupId
	 * @return
	 */
//	@Deprecated
//	List<JobHistoryModel> getManualRunningJobs(String groupId);
	
	List<JobHistoryModel> getManualRunning(String groupId);
	/**
	 * 删除Job任务
	 * @param jobId
	 * @return
	 * @throws GwtException
	 */
	void deleteJob(String jobId) throws GwtException;
	
	void addJobAdmin(String jobId,String uid)throws GwtException;
	
	void removeJobAdmin(String jobId,String uid)throws GwtException;
	
	List<ZUser> getJobAdmins(String jobId);
	
	void transferOwner(String jobId,String uid) throws GwtException;
	/**
	 * 移动Job
	 * 将job移动到新的group下
	 * @param jobId
	 * @param newGroupId
	 * @throws GwtException
	 */
	void move(String jobId,String newGroupId) throws GwtException;
	/**
	 * 同步任务脚本
	 * 给开发中心使用，方便开发中心直接同步脚本到调度中心
	 * @param jobId
	 * @param script
	 * @throws GwtException
	 */
	void syncScript(String jobId,String script) throws GwtException;
	
	
	/**
	 * 获得该JOB ID下面的的所有ACTIONDI
	 * 给开发中心使用，方便开发中心直接同步脚本到调度中心
	 * @param jobId
	 * @param script
	 * @return 
	 * @throws GwtException
	 */
	List<Long> getJobACtion(String id);
}
