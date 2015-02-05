package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.store.mysql.persistence.ZeusUser;


public interface UserManager{
	
	public List<ZeusUser> getAllUsers();
	
	public List<ZeusUser> findAllUsers(String sortField, String sortOrder);
	
	public ZeusUser findByUid(String uid);
	
	public List<ZeusUser> findListByUid(List<String> uids);
	
	public List<ZeusUser> findListByFilter(String filter, String sortField, String sortOrder);
	
	public ZeusUser addOrUpdateUser(ZeusUser user);
}
