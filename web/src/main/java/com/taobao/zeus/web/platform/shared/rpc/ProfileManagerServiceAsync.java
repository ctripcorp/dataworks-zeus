package com.taobao.zeus.web.platform.shared.rpc;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.taobao.zeus.web.platform.client.module.profile.ProfileModel;

public interface ProfileManagerServiceAsync {

	void updateHadoopConf(Map<String, String> conf, AsyncCallback<Void> callback);

	void getProfile(AsyncCallback<ProfileModel> callback);

}
