package com.taobao.zeus.model;

import java.util.List;

public class HostGroupCache {
	
	private String id;
	
	private String name;
	
	private String description;
	
	private List<String> hosts;
	
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<String> getHosts() {
		return hosts;
	}
	
	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}
}
