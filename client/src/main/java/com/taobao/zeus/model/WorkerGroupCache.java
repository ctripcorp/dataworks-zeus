package com.taobao.zeus.model;

import java.util.List;

public class WorkerGroupCache {
	
	private int id;
	
	private String name;
	
	private boolean effective;
	
	private String description;
	
	private List<String> hosts;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isEffective() {
		return effective;
	}
	
	public void setEffective(boolean effective) {
		this.effective = effective;
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
