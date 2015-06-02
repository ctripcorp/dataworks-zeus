package com.taobao.zeus.web.platform.client.util;

import java.io.Serializable;

public class ZUser implements Serializable{

	private static final long serialVersionUID = 1L;
	private String uid;
	private String name;
	private String email;
	private String phone;

	private String userType;
	private String isEffective;
	private String description;
	
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getIsEffective() {
		return isEffective;
	}
	public void setIsEffective(String isEffective) {
		this.isEffective = isEffective;
	}
}
