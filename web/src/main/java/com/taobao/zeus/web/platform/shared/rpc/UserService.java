package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.taobao.zeus.web.platform.client.util.ZUser;

@RemoteServiceRelativePath("user.rpc")
public interface UserService extends RemoteService {

	ZUser getUser();
	
	List<ZUser> getAllUsers();
	
	
	String checkUser(String username);
}
