package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;

public interface FileManagerServiceAsync {

	void addFile(String parentId, String name, boolean folder,
			AsyncCallback<FileModel> callback);

	void deleteFile(String fileId, AsyncCallback<Void> callback);

	void getFile(String id, AsyncCallback<FileModel> callback);

	void updateFileContent(String fileId, String content,
			AsyncCallback<Void> callback);

	void updateFileName(String fileId, String name, AsyncCallback<Void> callback);

	void getUserFiles(AsyncCallback<FileClientBean> callback);

	void moveFile(String sourceId, String targetId, AsyncCallback<Void> callback);

	void getCommonFiles(FileModel fm, AsyncCallback<List<FileModel>> callback);

	void getHomeFile(String id, AsyncCallback<FileModel> callback);

}
