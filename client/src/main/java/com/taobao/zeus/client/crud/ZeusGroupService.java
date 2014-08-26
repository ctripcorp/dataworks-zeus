package com.taobao.zeus.client.crud;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.GroupDescriptor;

public interface ZeusGroupService {
	/**
	 * 创建一个Group
	 * @param user 创建人
	 * @param groupName 组名称
	 * @param parentGroup 上一级组
	 * @param isDirectory 是否为目录
	 * @return
	 * @throws ZeusException
	 */
	public GroupDescriptor createGroup(String user,String groupName,String parentGroup,boolean isDirectory) throws ZeusException;
	
	public void updateGroup(String uid,GroupDescriptor groupDescriptor) throws ZeusException;
	
	public void deleteGroup(String uid,String groupId) throws ZeusException;
	
	public GroupDescriptor getGroupDescriptor(String groupId);
}
