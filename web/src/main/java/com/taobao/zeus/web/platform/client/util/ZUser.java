package com.taobao.zeus.web.platform.client.util;

import java.io.Serializable;

public class ZUser implements Serializable{

	private static final long serialVersionUID = 1L;
	private String uid;
	private String name;
	private boolean Super=false;
	public boolean isSuper() {
		return Super;
	}
	public void setSuper(boolean super1) {
		Super = super1;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
}
