package com.taobao.zeus.web.platform.client.module.filemanager;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.taobao.zeus.web.platform.client.module.filemanager.OpenFileEvent.OpenFileHandler;
public class OpenFileEvent extends GwtEvent<OpenFileHandler>{
	public static interface OpenFileHandler extends EventHandler{
		void onOpenFile(OpenFileEvent event);
	}
	private final FileModel model;
	public static Type<OpenFileHandler> TYPE=new Type<OpenFileHandler>();
	public OpenFileEvent(FileModel model){
		this.model=model;
	}
	
	@Override
	protected void dispatch(OpenFileHandler handler) {
		handler.onOpenFile(this);
	}

	@Override
	public Type<OpenFileHandler> getAssociatedType() {
		return TYPE;
	}

	public FileModel getModel() {
		return model;
	}


}
