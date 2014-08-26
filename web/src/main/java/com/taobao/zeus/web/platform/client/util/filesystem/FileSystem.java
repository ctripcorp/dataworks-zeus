package com.taobao.zeus.web.platform.client.util.filesystem;

import com.google.gwt.core.shared.GWT;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.util.PlatformBus;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.util.async.PlatformAsyncCallback;
import com.taobao.zeus.web.platform.shared.rpc.FileManagerService;
import com.taobao.zeus.web.platform.shared.rpc.FileManagerServiceAsync;

public class FileSystem {
	private PlatformContext context;
	public FileSystem(PlatformContext context){
		this.context=context;
	}
	
	private static FileManagerServiceAsync fileManagerService=GWT.create(FileManagerService.class);

	public void addFile(String parentId, String name, boolean folder,
			final PlatformAsyncCallback<FileModel> callback) {
		
		fileManagerService.addFile(parentId, name, folder, new AbstractAsyncCallback<FileModel>() {
			@Override
			public void onSuccess(FileModel f) {
				if(callback!=null){
					callback.callback(f);
				}
			}
		});
	}

	public void deleteFile(String fileId,final PlatformAsyncCallback<Void> callback) {
		fileManagerService.deleteFile(fileId, new AbstractAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void arg0) {
				if(callback!=null){
					callback.callback(null);
				}
			}
		});
	}

	public void updateFileContent(String fileId, String content,
			final PlatformAsyncCallback<Void> callback) {
		fileManagerService.updateFileContent(fileId, content, new AbstractAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void arg0) {
				if(callback!=null){
					callback.callback(arg0);
				}
			}
		});
	}

	public void updateFileName(final String fileId, String name,
			final PlatformAsyncCallback<Void> callback) {
		fileManagerService.updateFileName(fileId, name, new AbstractAsyncCallback<Void>() {
			public void onSuccess(Void model) {
				if(callback!=null){
					callback.callback(model);
				}
				fileManagerService.getFile(fileId, new AbstractAsyncCallback<FileModel>() {
					@Override
					public void onSuccess(FileModel arg0) {
						context.getPlatformBus().fireEvent(new FileUpdateEvent(arg0));						
					}
				});
			}
		});
		
	}

	public void getFile(String id,final PlatformAsyncCallback<FileModel> callback) {
		fileManagerService.getFile(id, new AbstractAsyncCallback<FileModel>() {
			@Override
			public void onSuccess(FileModel fm) {
				if(callback!=null){
					callback.callback(fm);
				}
			}
		});
	}
	
	public void moveFile(String sourceId,String targetId,final PlatformAsyncCallback<Void> callback){
		fileManagerService.moveFile(sourceId, targetId, new AbstractAsyncCallback<Void>() {
			public void onSuccess(Void arg0) {
				if(callback!=null){
					callback.callback(arg0);
				}
			}
		});
	}

}
