package com.taobao.zeus.web.platform.shared.rpc;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.taobao.zeus.web.platform.client.module.profile.ProfileModel;
import com.taobao.zeus.web.platform.client.util.GwtException;

@RemoteServiceRelativePath("profile.rpc")
public interface ProfileManagerService extends RemoteService{

	void updateHadoopConf(Map<String, String> conf) throws GwtException;
	
	ProfileModel getProfile();
}
