package com.taobao.zeus.web.platform.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupJobTreeModel;

public interface TreeServiceAsync {

	void follow(int type, String targetId, AsyncCallback<Void> callback);

	void getDependeeTree(String jobId, AsyncCallback<GroupJobTreeModel> callback);

	void getDependeeTreeJson(String jobId, AsyncCallback<String> callback);
	

	void getDependerTree(String jobId, AsyncCallback<GroupJobTreeModel> callback);

	void getMyTreeData(AsyncCallback<GroupJobTreeModel> callback);

	void getTreeData(AsyncCallback<GroupJobTreeModel> callback);

	void unfollow(int type, String targetId, AsyncCallback<Void> callback);

	void getDependerTreeJson(String jobId, AsyncCallback<String> callback);

}
