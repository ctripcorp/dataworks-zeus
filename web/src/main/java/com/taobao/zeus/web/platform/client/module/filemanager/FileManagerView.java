package com.taobao.zeus.web.platform.client.module.filemanager;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.data.shared.TreeStore;

public interface FileManagerView extends IsWidget{

	  public void collapse();

	  public void editName(FileModel childFileModel);

	  public void expand();

	  public FileModel getSelectedItem();

	  public List<FileModel> getSelectedItems();

	  public void selectFileModel(FileModel fileModel);
	  
	  public TreeStore<FileModel> getMyTreeStore();
	  
	  public void setMyActivity();
	  
	  public void setSharedActivity();
}
