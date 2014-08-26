package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;

@RemoteServiceRelativePath("file.rpc")
public interface FileManagerService extends RemoteService{
	FileModel addFile(String parentId, String name, boolean folder);

	public void deleteFile(String fileId) ;

	void updateFileContent(String fileId, String content);

	void updateFileName(String fileId, String name);

	public FileModel getFile(String id);

	FileClientBean getUserFiles();
	
	void moveFile(String sourceId,String targetId);
	
	List<FileModel> getCommonFiles(FileModel fm);
}
