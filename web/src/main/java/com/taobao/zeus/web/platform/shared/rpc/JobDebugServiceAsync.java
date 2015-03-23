package com.taobao.zeus.web.platform.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryModel;

public interface JobDebugServiceAsync {

	void debug(String fileId, String mode, String content,
			String hostGroupId, AsyncCallback<String> callback);

	void getLog(String debugId, AsyncCallback<String> callback);

	void getStatus(String debugId, AsyncCallback<String> callback);

	void getDebugHistory(PagingLoadConfig loadConfigString, String fileId,
			AsyncCallback<PagingLoadResult<DebugHistoryModel>> callback);

	void cancelDebug(String debugId, AsyncCallback<Void> callback);

	void getHistoryModel(String debugId,
			AsyncCallback<DebugHistoryModel> callback);

}
