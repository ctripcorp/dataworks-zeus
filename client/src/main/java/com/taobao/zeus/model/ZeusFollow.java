package com.taobao.zeus.model;

/**
 * 用户关注的Group或者Job
 * @author zhoufang
 *
 */
public class ZeusFollow {
	
	public static final Integer GroupType=1;
	public static final Integer JobType=2;
	private Long id;
	/**
	 * 关注的类型
	 * 1：group  2：Job
	 */
	private Integer type;
	
	private String uid;
	
	/**
	 * 关注的目标id
	 * 如果关注group  则这里是group id
	 * 如果关注的是Job  则这里是Job id
	 */
	private String targetId;

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

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
