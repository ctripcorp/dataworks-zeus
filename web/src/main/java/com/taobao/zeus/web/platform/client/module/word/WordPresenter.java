package com.taobao.zeus.web.platform.client.module.word;

import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.util.async.PlatformAsyncCallback;
import com.taobao.zeus.web.platform.client.util.place.PlaceHandler;

public interface WordPresenter extends Presenter,PlaceHandler{
	
	public static final String TAG="Word";
	/**
	 * 打开一个文档
	 * 如果这个文档已经打开，则将这个文档设置为焦点
	 * @param fm
	 * @param callback
	 */
	void open(String fileId,PlatformAsyncCallback<FileModel> callback);
	
	/**
	 * 更新已经打开的文档状态记录
	 * @param t
	 */
	public void updateLastOpen();
	
}
