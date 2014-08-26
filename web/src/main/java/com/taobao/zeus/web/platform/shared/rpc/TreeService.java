package com.taobao.zeus.web.platform.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupJobTreeModel;
/**
 * 提供给左侧树形结构的服务
 * @author zhoufang
 *
 */
@RemoteServiceRelativePath("tree.rpc")
public interface TreeService extends RemoteService{

	public GroupJobTreeModel getTreeData();
	
	public GroupJobTreeModel getMyTreeData();
	/**
	 * 任务依赖树结构
	 * @param jobId
	 * @return
	 */
	public GroupJobTreeModel getDependeeTree(String jobId);
	/**
	 * 任务依赖树结构
	 * @param jobId
	 * @return
	 */
	public String getDependeeTreeJson(String jobId);
	
	public String getDependerTreeJson(String jobId);
	/**
	 * 任务被依赖树结构
	 * @param jobId
	 * @return
	 */
	public GroupJobTreeModel getDependerTree(String jobId);
	
	public void follow(int type,String targetId);
	
	
	public void unfollow(int type,String targetId);
	
}
