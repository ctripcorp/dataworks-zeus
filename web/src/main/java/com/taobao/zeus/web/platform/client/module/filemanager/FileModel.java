package com.taobao.zeus.web.platform.client.module.filemanager;

import java.io.Serializable;
import java.util.Date;

public class FileModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String parentId;
	private Date createDate;
	private Date modifiedDate;
	private String name;
	private boolean folder;
	private String content;
	private String owner;
	private boolean admin;
	private String workerGroupId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public String getWorkerGroupId() {
		return workerGroupId;
	}
	public void setWorkerGroupId(String workerGroupId) {
		this.workerGroupId = workerGroupId;
	}
}