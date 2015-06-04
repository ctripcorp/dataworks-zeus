package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.taobao.zeus.web.platform.client.util.GwtException;
import com.taobao.zeus.web.platform.client.util.ZUser;

@RemoteServiceRelativePath("user.rpc")
public interface UserService extends RemoteService {

	ZUser getUser();
	
	List<ZUser> getAllUsers();
	
	List<ZUser> getAllGroupUsers();
	
	PagingLoadResult<ZUser> getUsersPaging(PagingLoadConfig config,
			String filter);
	
	ZUser updateUser(ZUser zu) throws GwtException;
	
	String checkUser(String username,String password);
	
	String checkUserSession();

	void checkpass(List<String> uids) throws GwtException;
	
	void checknotpass(List<String> uids) throws GwtException;
	
	void delete(List<String> uids) throws GwtException;
}
