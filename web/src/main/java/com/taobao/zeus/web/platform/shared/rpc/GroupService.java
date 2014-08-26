package com.taobao.zeus.web.platform.shared.rpc;

import java.io.IOException;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.util.GwtException;
import com.taobao.zeus.web.platform.client.util.ZUser;

@RemoteServiceRelativePath("group.rpc")
public interface GroupService extends RemoteService {
	/**
	 * 创建一个分组
	 * @param group
	 * @throws ServiceException
	 * @throws IOException
	 */
	public String createGroup(String groupName,String parentGroupId,boolean isDirectory) throws GwtException;
	/**
	 * 根据名称获取相应的分组
	 * @param name
	 * @return
	 * @throws ServiceException
	 */
	GroupModel getGroup(String groupId) throws GwtException;
	/**
	 * 
	 * @param groupId
	 * @return
	 * @throws GwtException
	 */
	public GroupModel getUpstreamGroup(String groupId) throws GwtException;

	/**
	 * 删除分组
	 * @param user
	 * @param groupName
	 * @throws ServiceException
	 */
	void deleteGroup(String groupId) throws GwtException;
	/**
	 * 更新组信息
	 * @param group
	 * @throws GwtException
	 */
	void updateGroup(GroupModel group) throws GwtException;
	
	List<ZUser> getGroupAdmins(String groupId);
	
	void addGroupAdmin(String groupId,String uid) throws GwtException;
	
	void removeGroupAdmin(String groupId,String uid) throws GwtException;
	
	void transferOwner(String groupId,String uid) throws GwtException;
	/**
	 * 移动组
	 * @param groupId
	 * @param newParentGroupId
	 * @throws GwtException
	 */
	void move(String groupId,String newParentGroupId) throws GwtException;
}
