package com.taobao.zeus.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profile {
	
	private String id;

	private String uid;
	
	private Map<String, String> hadoopConf=new HashMap<String, String>();
	
	private Date gmtCreate=new Date();
	
	private Date gmtModified=new Date();

	public Map<String, String> getHadoopConf() {
		return hadoopConf;
	}

	public void setHadoopConf(Map<String, String> hadoopConf) {
		this.hadoopConf = hadoopConf;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
}
