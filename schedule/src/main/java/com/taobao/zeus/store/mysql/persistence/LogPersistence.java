package com.taobao.zeus.store.mysql.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="zeus_log")
public class LogPersistence implements Serializable{
	private static final long serialVersionUID = 1L;	
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="logtype")
	private String logType;
	
	@Column(name="createtime")
	private Date createTime=new Date();
	
	@Column(name="username")
	private String userName;
	
	@Column(name="ip")
	private String ip;
	
	@Column(name="url")
	private String url;
	
	@Column(name="rpc")
	private String rpc;
	
	@Column(name="delegate")
	private String delegate;
	
	@Column(name="method")
	private String method;
	
	@Column(name="description")
	private String description;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRpc() {
		return rpc;
	}

	public void setRpc(String rpc) {
		this.rpc = rpc;
	}

	public String getDelegate() {
		return delegate;
	}

	public void setDelegate(String delegate) {
		this.delegate = delegate;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
