package com.taobao.zeus.store.mysql.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="zeus_profile")
public class ProfilePersistence {
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String uid;
	@Column(name="hadoop_conf")
	private String hadoopConf;
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
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getHadoopConf() {
		return hadoopConf;
	}
	public void setHadoopConf(String hadoopConf) {
		this.hadoopConf = hadoopConf;
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
