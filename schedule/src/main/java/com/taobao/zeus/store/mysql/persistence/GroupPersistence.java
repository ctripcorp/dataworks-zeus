package com.taobao.zeus.store.mysql.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="zeus_group")
public class GroupPersistence implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column
	private Integer parent;
	@Column(nullable=false)
	private String name;
	@Column(nullable=false)
	private String owner;
	@Column
	private String descr;
	/**
	 * 0: 是目录
	 * 1：不是目录
	 */
	@Column(nullable=false)
	private int directory;
	@Column(length=4096)
	private String configs;
	@Column(length=4096)
	private String resources;
	@Column(name="gmt_create",nullable=false)
	private Date gmtCreate=new Date();
	@Column(name="gmt_modified",nullable=false)
	private Date gmtModified=new Date();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public int getDirectory() {
		return directory;
	}
	public void setDirectory(int directory) {
		this.directory = directory;
	}
	public String getConfigs() {
		return configs;
	}
	public void setConfigs(String configs) {
		this.configs = configs;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public Integer getParent() {
		return parent;
	}
	public void setParent(Integer parent) {
		this.parent = parent;
	}
	public String getResources() {
		return resources;
	}
	public void setResources(String resources) {
		this.resources = resources;
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