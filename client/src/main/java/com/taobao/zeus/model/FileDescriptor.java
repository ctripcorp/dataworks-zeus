package com.taobao.zeus.model;

import java.io.Serializable;
import java.util.Date;

public class FileDescriptor implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String owner;
	private String parent;
	private boolean folder;
	private String content;
	private Date gmtCreate=new Date();
	private Date gmtModified=new Date();
	private String workerGroupId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public boolean isFolder() {
		return folder;
	}
	public void setFolder(boolean folder) {
		this.folder = folder;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getWorkerGroupId() {
		return workerGroupId;
	}
	public void setWorkerGroupId(String workerGroupId) {
		this.workerGroupId = workerGroupId;
	}
}
