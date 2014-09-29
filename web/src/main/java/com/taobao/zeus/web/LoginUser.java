package com.taobao.zeus.web;

import com.taobao.zeus.store.mysql.persistence.ZeusUser;

public class LoginUser {
	public static ThreadLocal<ZeusUser> user=new ThreadLocal<ZeusUser>();
	
	public static ZeusUser getUser(){
	
//		System.out.println(Thread. currentThread ().getName() + ":" + user.get().getUid());
		return user.get();
	}

	public static void setUser(ZeusUser user) {
//		System.out.println(user.toString());
		LoginUser.user.set(user);
	}
	
}
