package com.taobao.zeus.store.mysql.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="zeus_permission")
public class PermissionPersistence {
	
	public static final String GROUP_TYPE="group";
	public static final String JOB_TYPE="job";
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String type;
	@Column(name="target_id")
	private Long targetId;
	@Column
	private String uid;
	@Column(name="gmt_create")
	private Date gmtCreate=new Date();
	@Column(name="gmt_modified")
	private Date gmtModified=new Date();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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
