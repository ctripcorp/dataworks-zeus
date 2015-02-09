package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.store.mysql.persistence.ZeusUser;


public interface UserManager{
	
	public List<ZeusUser> getAllUsers();
	
	public ZeusUser findByUid(String uid);
	
	public List<ZeusUser> findListByUid(List<String> uids);
	
	public ZeusUser addOrUpdateUser(ZeusUser user);
	
	//2015-02-04 add--------
	public ZeusUser findByUidFilter(String uid);
	
	public List<ZeusUser> findAllUsers(String sortField, String sortOrder);
	
	public List<ZeusUser> findListByFilter(String filter, String sortField, String sortOrder);
}
