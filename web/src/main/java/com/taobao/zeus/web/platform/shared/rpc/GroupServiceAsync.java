package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.util.ZUser;

public interface GroupServiceAsync {

	void addGroupAdmin(String groupId, String uid, AsyncCallback<Void> callback);

	void createGroup(String groupName, String parentGroupId,
			boolean isDirectory, AsyncCallback<String> callback);

	void deleteGroup(String groupId, AsyncCallback<Void> callback);

	void getGroup(String groupId, AsyncCallback<GroupModel> callback);

	void getGroupAdmins(String groupId, AsyncCallback<List<ZUser>> callback);

	void getUpstreamGroup(String groupId, AsyncCallback<GroupModel> callback);

	void removeGroupAdmin(String groupId, String uid,
			AsyncCallback<Void> callback);

	void transferOwner(String groupId, String uid, AsyncCallback<Void> callback);

	void updateGroup(GroupModel group, AsyncCallback<Void> callback);

	void move(String groupId, String newParentGroupId,
			AsyncCallback<Void> callback);

}
