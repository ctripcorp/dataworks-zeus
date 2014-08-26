package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 带层次的Model 左侧树形菜单展示使用 包含了Group数据和Job数据
 * 
 * @author Administrator
 * 
 */
public class GroupJobTreeModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String id;
	private boolean group;
	private boolean directory;
	private boolean job;
	private boolean follow;
	private String owner;
	
	private List<GroupJobTreeModel> children=new ArrayList<GroupJobTreeModel>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isGroup() {
		return group;
	}
	public void setGroup(boolean group) {
		this.group = group;
	}
	public boolean isDirectory() {
		return directory;
	}
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
	public boolean isJob() {
		return job;
	}
	public void setJob(boolean job) {
		this.job = job;
	}
	public boolean isFollow() {
		return follow;
	}
	public void setFollow(boolean follow) {
		this.follow = follow;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public List<GroupJobTreeModel> getChildren() {
		return children;
	}
	public void setChildren(List<GroupJobTreeModel> children) {
		this.children = children;
	}
}
