package com.taobao.zeus.web.platform.shared.rpc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;

public class FileClientBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private FileClientBean parent;
	private List<FileClientBean> subFiles=new ArrayList<FileClientBean>();
	private FileModel fileModel;
	
	public FileClientBean(){
		
	}
	
	public FileClientBean(FileModel fileModel){
		this.fileModel=fileModel;
	}
	
	public void addSubFile(FileClientBean bean){
		if(!subFiles.contains(bean)){
			subFiles.add(bean);
		}
	}

	public List<FileClientBean> getSubFiles() {
		return subFiles;
	}


	public FileClientBean getParent() {
		return parent;
	}

	public void setParent(FileClientBean parent) {
		this.parent = parent;
	}

	public FileModel getFileModel() {
		return fileModel;
	}

	public void setFileModel(FileModel fileModel) {
		this.fileModel = fileModel;
	}
}
