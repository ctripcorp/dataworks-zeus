package com.taobao.zeus.web.platform.shared.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryModel;
import com.taobao.zeus.web.platform.client.util.GwtException;
@RemoteServiceRelativePath("debug.rpc")
public interface JobDebugService extends RemoteService{

	String debug(String fileId,String mode,String content)throws GwtException;
	
	
	String getLog(String debugId);
	
	String getStatus(String debugId);
	
	PagingLoadResult<DebugHistoryModel> getDebugHistory(PagingLoadConfig loadConfigString, String fileId);
	
	public void cancelDebug(String debugId) throws GwtException;
	
	DebugHistoryModel getHistoryModel(String debugId);
}
