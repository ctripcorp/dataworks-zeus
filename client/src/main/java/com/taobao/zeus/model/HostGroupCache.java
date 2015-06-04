package com.taobao.zeus.model;

import java.util.List;

public class HostGroupCache {
	
	private String id;
	
	private String name;
	
	private String description;
	
	private List<String> hosts;
	
	private volatile int currentPositon;
	
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
	public int getCurrentPositon() {
		return currentPositon;
	}
	public void setCurrentPositon(int currentPositon) {
		this.currentPositon = currentPositon;
	}
	
	public String selectHost(){
		if (hosts==null) {
			return null;
		}
		int size = hosts.size();
		if (size == 0) {
			return null;
		}
		if (size == 1) {
			return hosts.get(0);
		}
		if (currentPositon >= size) {
			currentPositon = 0;
		}
		String host = hosts.get(currentPositon);
		currentPositon++;
		return host;
	}
	
}
