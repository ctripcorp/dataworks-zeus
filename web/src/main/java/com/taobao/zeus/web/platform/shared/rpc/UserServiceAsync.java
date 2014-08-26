package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.taobao.zeus.web.platform.client.util.ZUser;

public interface UserServiceAsync {

	void getAllUsers(AsyncCallback<List<ZUser>> callback);

	void getUser(AsyncCallback<ZUser> callback);

}
