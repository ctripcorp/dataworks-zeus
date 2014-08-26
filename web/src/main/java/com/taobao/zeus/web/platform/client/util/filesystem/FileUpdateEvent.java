package com.taobao.zeus.web.platform.client.util.filesystem;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.util.filesystem.FileUpdateEvent.FileUpdateHandler;;

public class FileUpdateEvent extends GwtEvent<FileUpdateHandler>{
	public static interface FileUpdateHandler extends EventHandler{
		void onFileUpdate(FileUpdateEvent event);
	}
	public static Type<FileUpdateHandler> TYPE=new Type<FileUpdateHandler>();
	
	private FileModel model;
	public FileUpdateEvent(FileModel model){
		this.model=model;
	}
	
	@Override
	protected void dispatch(FileUpdateHandler handler) {
		handler.onFileUpdate(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<FileUpdateHandler> getAssociatedType() {
		return TYPE;
	}

	public FileModel getModel() {
		return model;
	}

}
