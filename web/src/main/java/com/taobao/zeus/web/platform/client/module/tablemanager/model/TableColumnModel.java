package com.taobao.zeus.web.platform.client.module.tablemanager.model;

import java.io.Serializable;

/**
 * @author gufei.wzy 2012-9-17
 */

public class TableColumnModel implements Serializable {
	private static final long serialVersionUID = 9154611086109470482L;
	private String name;
	private String type;
	private String desc;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}