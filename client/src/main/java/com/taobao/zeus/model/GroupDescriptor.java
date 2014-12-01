package com.taobao.zeus.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class GroupDescriptor implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String parent;
	private String name;
	private String owner;
	private String desc;
	private boolean directory;
	private boolean isExisted;
	private Map<String, String> properties=new HashMap<String, String>();
	private List<Map<String,String>> resources=new ArrayList<Map<String,String>>();
	
	public String getDesc() {
		return desc;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}


	public boolean isDirectory() {
		return directory;
	}

	public void setDesc(String desc) {
		this.desc=desc;
	}

	public void setName(String name) {
		this.name=name;
	}

	public void setOwner(String owner) {
		this.owner=owner;
	}


	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<Map<String, String>> getResources() {
		return resources;
	}

	public void setResources(List<Map<String, String>> resources) {
		this.resources = resources;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}


	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	public boolean isExisted() {
		return isExisted;
	}

	public void setExisted(boolean isExisted) {
		this.isExisted = isExisted;
	}

	public Boolean IsExisted() {
		// TODO Auto-generated method stub
		return null;
	}
}
