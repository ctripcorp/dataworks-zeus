package com.taobao.zeus.web.platform.client.module.filemanager;

import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.util.place.PlaceHandler;

public interface FileManagerPresenter extends Presenter,PlaceHandler{

	public static final String TAG="Document";
	
	  boolean isEnableCreate();

	  boolean isEnableDelete();

	  boolean isEnableEditName();

	  boolean isEnableOpen();

	  void onCollapse();

	  void onCreate(boolean folder,String suffix);

	  void onDelete();

	  void onEditFileNameComplete(FileModel file);

	  void onEditName();

	  void onExpand();

	  void onOpen();

	  void onSelect(FileModel fileModel);
	  
}
