package com.taobao.zeus.web.platform.client.module.word;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;

public interface WordView extends IsWidget{
	/**
	 * 打开一个文档
	 * 如果已经打开，则将这个文档设置为焦点
	 * @param fm
	 */
	void open(FileModel fm);
	
	boolean contain(String fileId);
	
	void updateFileName(FileModel model);
	
	List<String> getOpenedDocs();
}
