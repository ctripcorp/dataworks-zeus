package com.taobao.zeus.store.mysql.persistence;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Query;

/**
 * 用户信息
 * @author zhoufang
 *
 */
@Entity(name="zeus_user")
public class ZeusUser{
	
	public static final ZeusUser ADMIN=new ZeusUser(){
		public String getEmail() {return "yangfei@Ctrip.com";};
		public String getName() {return "杨飞";};
		public String getPhone() {return "";};
		public String getUid() {return "yangfei";};
	};
	public static ZeusUser USER=new ZeusUser(null,null,null,null);

	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String name;
	@Column
	private String uid;
	@Column
	private String email;
	@Column
	private String password;
	@Column
	private String wangwang;
	@Column
	private String phone;
	@Column(name="gmt_create")
	private Date gmtCreate;
	@Column(name="gmt_modified")
	private Date gmtModified;
	public ZeusUser(String email, String name, String phone,
			String uid) {
		this.email = email;
		this.name = name;
		this.uid = uid;
		this.phone = phone;
		// TODO Auto-generated constructor stub
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ZeusUser() {
		// TODO Auto-generated constructor stub
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUid() {
		return this.uid;
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
	public String getWangwang() {
		return this.wangwang;
	}
	public void setWangwang(String wangwang) {
		this.wangwang = wangwang;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "ZeusUser [uid=" + uid + ", name=" + name + ", email=" + email + ", wangwang=" + wangwang + "]";
	}

}
