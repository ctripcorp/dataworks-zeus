package com.taobao.zeus.web.platform.server.rpc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.taobao.zeus.store.Super;
import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.web.LoginUser;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.shared.rpc.UserService;

public class UserServiceImpl implements UserService{
	@Autowired
	private UserManager userManager;
	@Override
	public ZUser getUser() {
		ZeusUser u= LoginUser.getUser();
		ZUser zu=new ZUser();
		zu.setName(u.getName());
		zu.setUid(u.getUid());
		zu.setSuper(Super.getSupers().contains(u.getUid()));
		return zu;
	}

	@Override
	public List<ZUser> getAllUsers() {
		List<ZUser> result=new ArrayList<ZUser>();
		List<ZeusUser> list=userManager.getAllUsers();
		for(ZeusUser u:list){
			ZUser zu=new ZUser();
			zu.setName(u.getName());
			zu.setUid(u.getUid());
			result.add(zu);
		}
		return result;
	}
	

}
