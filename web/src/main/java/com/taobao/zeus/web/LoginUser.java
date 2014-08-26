package com.taobao.zeus.web;

import com.taobao.zeus.store.mysql.persistence.ZeusUser;

public class LoginUser {
	static ThreadLocal<ZeusUser> user=new ThreadLocal<ZeusUser>();

	public static ZeusUser getUser(){
		return user.get();
	}
	
}
