package com.taobao.zeus.store.mysql.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/**
 * 用户关注的Group或者Job
 * @author zhoufang
 *
 */
@Entity(name="zeus_follow")
public class ZeusFollowPersistence {
	
	public static final Integer GroupType=1;
	public static final Integer JobType=2;
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String uid;
	/**
	 * 关注的类型
	 * 1：group  2：Job
	 */
	@Column
	private Integer type;
	
	/**
	 * 关注的目标id
	 * 如果关注group  则这里是group id
	 * 如果关注的是Job  则这里是Job id
	 */
	@Column(name="target_id")
	private Long targetId;
	@Column(name="gmt_create")
	private Date gmtCreate;
	@Column(name="gmt_modified")
	private Date gmtModified;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
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

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
}
