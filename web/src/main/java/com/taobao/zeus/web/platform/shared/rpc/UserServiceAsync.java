package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.taobao.zeus.web.platform.client.util.ZUser;

public interface UserServiceAsync {

	void getAllUsers(AsyncCallback<List<ZUser>> callback);

	void getUser(AsyncCallback<ZUser> callback);
	
	void checkUser(String username,String password,AsyncCallback<String> callback);
	
	void checkUserSession(AsyncCallback<String> callback);

	void updateUser(ZUser zu, AsyncCallback<ZUser> callback);

	void getUsersPaging(PagingLoadConfig config, String filter,
			AsyncCallback<PagingLoadResult<ZUser>> callback);

	void getAllGroupUsers(AsyncCallback<List<ZUser>> callback);

	void checkpass(List<String> uids, AsyncCallback<Void> callback);

	void checknotpass(List<String> uids, AsyncCallback<Void> callback);

	void delete(List<String> uids, AsyncCallback<Void> callback);

}