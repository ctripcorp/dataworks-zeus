package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.store.mysql.persistence.ZeusUser;


public interface UserManager{
	
	public List<ZeusUser> getAllUsers();
	
	public ZeusUser findByUid(String uid);
	
	public List<ZeusUser> findListByUid(List<String> uids);
	
	public ZeusUser addOrUpdateUser(ZeusUser user);
}
