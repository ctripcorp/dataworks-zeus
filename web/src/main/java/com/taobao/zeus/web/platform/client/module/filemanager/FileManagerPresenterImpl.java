package com.taobao.zeus.web.platform.client.module.filemanager;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.async.PlatformAsyncCallback;
import com.taobao.zeus.web.platform.client.util.filesystem.FSUtil;
import com.taobao.zeus.web.platform.client.util.place.PlatformPlaceChangeEvent;

public class FileManagerPresenterImpl implements FileManagerPresenter{

	private FileManagerView fileManagerView;
	private PlatformContext context;
	public FileManagerPresenterImpl(PlatformContext context){
		this.context=context;
		context.getPlatformBus().registPlaceHandler(this);
	}
	
	@Override
	public boolean isEnableCreate() {
		FileModel selectedItem = getFileManagerView().getSelectedItem();
	    boolean folder = selectedItem == null ? false : selectedItem.isFolder();
	    return folder;
	}

	@Override
	public boolean isEnableDelete() {
		 FileModel selectedItem = getFileManagerView().getSelectedItem();
		 if(selectedItem==null || selectedItem.getParentId()==null){
			 return false;
		 }
		 return true;
	}

	@Override
	public boolean isEnableEditName() {
		FileModel selectedItem = getFileManagerView().getSelectedItem();
	    return selectedItem != null && selectedItem.getParentId()!=null;
	}

	@Override
	public boolean isEnableOpen() {
		boolean isEnableOpen = false;
	    FileModel selectedItem = getFileManagerView().getSelectedItem();
	    if (selectedItem != null) {
	      if(!selectedItem.isFolder()){
	    	  isEnableOpen=true;
	      }
	    }
	    return isEnableOpen;
	}

	@Override
	public void onCollapse() {
		getFileManagerView().collapse();
	}

	@Override
	public void onCreate(boolean folder,String suffix) {
		final FileModel parentFileModel = getFileManagerView().getSelectedItem();
	    String name = FSUtil.getNextUntitledFileName(parentFileModel, folder,suffix,getFileManagerView().getMyTreeStore());
	    
	    context.getFileSystem().addFile(parentFileModel.getId(), name, folder, new PlatformAsyncCallback<FileModel>() {
	    	@Override
	    	public void callback(FileModel t) {
	    		getFileManagerView().getMyTreeStore().add(parentFileModel, t);
	    		getFileManagerView().selectFileModel(t);
	    	    getFileManagerView().editName(t);
	    	}
	    });
	}

	@Override
	public void onDelete() {
		List<FileModel> fileModels = getFileManagerView().getSelectedItems();
		if(!fileModels.isEmpty()){
			final FileModel model=fileModels.get(0);
			String msg="";
			if(model.isFolder()){
				msg="确定 删除文件夹 以及文件夹下所有的文件？";
			}else{
				msg="确定 删除文件:"+model.getName()+"？";
			}
			ConfirmMessageBox box=new ConfirmMessageBox("删除", msg);
			box.addHideHandler(new HideHandler() {
				public void onHide(HideEvent event) {
					Dialog dialog=(Dialog) event.getSource();
					if(DefaultMessages.getMessages().messageBox_yes().equals(dialog.getHideButton().getText())){
						context.getFileSystem().deleteFile(model.getId(), new PlatformAsyncCallback<Void>() {
							public void callback(Void t) {
								getFileManagerView().getMyTreeStore().remove(model);
							}
						});
					}
				}
			});
			box.show();
		}
	}

	@Override
	public void onEditFileNameComplete(FileModel file) {
		FileModel fileModel = getFileManagerView().getSelectedItem();
	    if (fileModel != null) {
	      fileModel.setModifiedDate(new Date());
	      context.getFileSystem().updateFileName(file.getId(), file.getName(),null);
	      context.getPlatformBus().fireEvent(new OpenFileEvent(file));
	    }
	}

	@Override
	public void onEditName() {
		 FileModel selectedItem = getFileManagerView().getSelectedItem();
	    if (selectedItem != null && selectedItem.getParentId()!=null) {
	    	getFileManagerView().editName(selectedItem);
	    }
	}

	@Override
	public void onExpand() {
		getFileManagerView().expand();
	}

	@Override
	public void onOpen() {
		List<FileModel> fileModels = getFileManagerView().getSelectedItems();
	    for (FileModel fileModel : fileModels) {
	    	context.getPlatformBus().fireEvent(new OpenFileEvent(fileModel));
	    }
	}

	@Override
	public void onSelect(FileModel fileModel) {
//		getDesktopBus().fireSelectFileModelEvent(new SelectFileModelEvent(fileModel));
	}

	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(getFileManagerView().asWidget());
	}

	private FileManagerView getFileManagerView() {
		if(fileManagerView==null){
			fileManagerView=new FileManagerViewImpl(context,this);
		}
		return fileManagerView;
	}

	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}

	@Override
	public void handle(PlatformPlaceChangeEvent event) {
		String value=event.getNewPlace().getCurrent().value;
		if(value.equalsIgnoreCase("mydoc")){
			fileManagerView.setMyActivity();
		}else if(value.equalsIgnoreCase("shareddoc")){
			fileManagerView.setSharedActivity();
		}
	}

	@Override
	public String getHandlerTag() {
		return TAG;
	}

}
