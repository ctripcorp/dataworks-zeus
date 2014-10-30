package com.taobao.zeus.store.mysql.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="zeus_file")
public class FilePersistence  implements Serializable{
	private static final long serialVersionUID = 1L;

	public static int FOLDER=1;
	public static int FILE=2;
	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable=false)
	private String name;
	@Column(nullable=false)
	private String owner;
	
	private Long parent;
	@Column(nullable=false)
	private int type;
	
	@Column(length=16777215)
	private String content;
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

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
