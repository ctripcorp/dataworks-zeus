package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.store.mysql.persistence.ZeusUser;


public interface UserManager{
	
	public List<ZeusUser> getAllUsers();
	
	public ZeusUser findByUid(String uid);
	
	public List<ZeusUser> findListByUid(List<String> uids);
	
	public ZeusUser addOrUpdateUser(ZeusUser user);
	/**
	 * 按照uids的顺序返回user列表
	 * @param uids
	 * @return
	 */
	public List<ZeusUser> findListByUidByOrder(List<String> uids);
}
