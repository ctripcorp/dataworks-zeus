package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupModel implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String parent;
	private String name;
	private String owner;
	private List<String> owners=new ArrayList<String>();
	private String ownerName;
	private String desc;
	private boolean directory;
	private Map<String, String> allProperties=new HashMap<String, String>();
	private Map<String, String> localProperties=new HashMap<String, String>();
	private List<Map<String, String>> allResources=new ArrayList<Map<String,String>>();
	private List<Map<String, String>> localResources=new ArrayList<Map<String,String>>();
	private boolean admin;
	private List<String> admins=new ArrayList<String>();
	private List<String> follows=new ArrayList<String>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
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
	public List<String> getOwners() {
		return owners;
	}
	public void setOwners(List<String> owners) {
		this.owners = owners;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Map<String, String> getAllProperties() {
		return allProperties;
	}
	public void setAllProperties(Map<String, String> allProperties) {
		this.allProperties = allProperties;
	}
	public Map<String, String> getLocalProperties() {
		return localProperties;
	}
	public void setLocalProperties(Map<String, String> localProperties) {
		this.localProperties = localProperties;
	}
	public List<Map<String, String>> getAllResources() {
		return allResources;
	}
	public void setAllResources(List<Map<String, String>> allResources) {
		this.allResources = allResources;
	}
	public List<Map<String, String>> getLocalResources() {
		return localResources;
	}
	public void setLocalResources(List<Map<String, String>> localResources) {
		this.localResources = localResources;
	}
	public List<String> getAdmins() {
		return admins;
	}
	public void setAdmins(List<String> admins) {
		this.admins = admins;
	}
	public List<String> getFollows() {
		return follows;
	}
	public void setFollows(List<String> follows) {
		this.follows = follows;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public boolean isDirectory() {
		return directory;
	}
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
}
