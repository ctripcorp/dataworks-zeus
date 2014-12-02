package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.model.ZeusFollow;


public interface FollowManagerOld {
	/**
	 * 新增一个关注
	 * @param uid
	 * @param type
	 * @param targetId
	 * @return
	 */
	ZeusFollow addFollow(String uid,Integer type,String targetId) ;
	/**
	 * 删除一个关注
	 * @param uid
	 * @param type
	 * @param targetId
	 * @return
	 */
	void deleteFollow(String uid,Integer type,String targetId);
	/**
	 * 查询用户的所有关注
	 * @param uid
	 * @return
	 */
	List<ZeusFollow> findAllTypeFollows(String uid);
	/**
	 * 查询关注的所有Group
	 * @param uid
	 * @return
	 */
	List<ZeusFollow> findFollowedGroups(String uid);
	/**
	 * 查询关注的所有Job
	 * @param uid
	 * @return
	 */
	List<ZeusFollow> findFollowedJobs(String uid);
	/**
	 * 查询关注该Job的人员名单
	 * @param jobId
	 * @return
	 */
	List<ZeusFollow> findJobFollowers(String jobId);
	/**
	 * 查询关注该group的人员名单
	 * @param groupId
	 * @return
	 */
	List<ZeusFollow> findGroupFollowers(List<String> groupIds);
	/**
	 * 查询实际关注该job的人
	 * 综合考虑了job自身被关注的人，以及上层group被关注的人
	 * @param jobId
	 * @return
	 */
	List<String> findActualJobFollowers(String jobId);
}
