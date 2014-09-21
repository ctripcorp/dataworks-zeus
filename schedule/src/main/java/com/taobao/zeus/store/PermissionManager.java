package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;

public interface PermissionManager {

	Boolean hasGroupPermission(String user,String groupId);
	
	Boolean hasJobPermission(String user,String jobId);
	
	Boolean hasActionPermission(String user,String jobId);
	/**
	 * 添加组的管理员
	 * @param user 被授权人 
	 */
	void addGroupAdmin(String user,String groupId) throws ZeusException;
	/**
	 * 删除组管理员
	 * @param user 被授权人
	 */
	void removeGroupAdmin(String user,String groupId) throws ZeusException;
	
	void addJobAdmin(String user,String jobId) throws ZeusException;
	
	void removeJobAdmin(String user,String jobId) throws ZeusException;
	/**
	 * 该组的管理员名单
	 * @param groupId
	 * @return
	 */
	List<String> getGroupAdmins(String groupId);
	/**
	 * 该Job的管理员名单
	 * @param jobId
	 * @return
	 */
	List<String> getJobAdmins(String jobId);
	/**
	 * 该Job的同系列任务
	 * @param jobId
	 * @return
	 */
	List<Long> getJobACtion(String jobId);
}
