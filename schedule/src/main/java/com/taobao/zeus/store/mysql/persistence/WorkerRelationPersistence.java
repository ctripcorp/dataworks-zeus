package com.taobao.zeus.store.mysql.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="zeus_worker_relation")
public class WorkerRelationPersistence {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable=false)
	private String host;
	
	@Column(name = "worker_group_id", nullable = false)
	private Integer workerGroupId;
	
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

	public Integer getWorkerGroupId() {
		return workerGroupId;
	}

	public void setWorkerGroupId(Integer workerGroupId) {
		this.workerGroupId = workerGroupId;
	}
	
	@Override
	public String toString() {
		return host + " : " + workerGroupId;
	}
}
