package com.taobao.zeus.web.platform.client.util;

import java.io.Serializable;

public class ZUserContactTuple implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private ZUser zuser;
	private Boolean isImportant;
	
	public ZUserContactTuple(){}
	
	public ZUserContactTuple(ZUser zuser, Boolean isImportant) {
		this.zuser = zuser;
		this.isImportant = isImportant;
	}
	

	@Override
	public String toString() {
		return "[" + getZuser().toString() + "," + getIsImportant().toString() + "]";
	}

	public ZUser getZuser() {
		return zuser;
	}

	public Boolean getIsImportant() {
		return isImportant;
	}


}