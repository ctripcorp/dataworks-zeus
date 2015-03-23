package com.taobao.zeus.store.mysql.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="zeus_host_relation")
public class HostRelationPersistence {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable=false)
	private String host;
	
	@Column(name = "host_group_id", nullable = false)
	private Integer hostGroupId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getHostGroupId() {
		return hostGroupId;
	}

	public void setHostGroupId(Integer hostGroupId) {
		this.hostGroupId = hostGroupId;
	}
	
	@Override
	public String toString() {
		return host + " : " + hostGroupId;
	}
}
