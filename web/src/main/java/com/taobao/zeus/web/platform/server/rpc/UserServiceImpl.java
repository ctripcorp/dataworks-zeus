package com.taobao.zeus.web.platform.server.rpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.taobao.zeus.store.Super;
import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.web.LoginUser;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.shared.rpc.UserService;
import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.GroupDescriptor;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobDescriptor.JobRunType;
import com.taobao.zeus.model.JobDescriptor.JobScheduleType;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.processer.DownloadProcesser;
import com.taobao.zeus.model.processer.Processer;
import com.taobao.zeus.store.GroupBean;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.GroupManagerTool;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.mysql.persistence.GroupPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.Worker;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.store.mysql.tool.GroupValidate;
import com.taobao.zeus.store.mysql.tool.JobValidate;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvert;

public class UserServiceImpl extends RemoteServiceServlet implements UserService{
	@Autowired
	private UserManager userManager;
	@Override
	public ZUser getUser() {
		ZeusUser u= LoginUser.getUser();//System.out.println(u);
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
	@Override
	public String checkUser(String username) {
		//ZeusUser u= LoginUser.getUser();
		//HttpServletRequest httpRequest
		ZeusUser u = userManager.findByUid(username);
//		System.out.println(u);
		if(null == u){
			return "null";
		}else{
//			ZeusUser zeusUser=null;
//			zeusUser=new ZeusUser();
//			zeusUser.setEmail(u.getEmail());
//			zeusUser.setUid(u.getUid());
//			zeusUser.setName(u.getName());
//			zeusUser.setPhone(u.getPhone());
			String uid = u.getUid();
//			if(!uid.equals(httpRequest.getSession().getAttribute("user"))){
//				userManager.addOrUpdateUser(zeusUser);
//				httpRequest.getSession().setAttribute("user", zeusUser.getUid());
//			}
//			LoginUser.user.set(zeusUser);
			ZeusUser.USER.setUid(uid);
			ZeusUser.USER.setEmail(u.getEmail());
			ZeusUser.USER.setName(u.getName());
			ZeusUser.USER.setPhone(u.getPhone());

			return uid;
		}
		
		
	
	}


	public String checkUserSession() {
		//System.out.println("get session");
		//HttpSession httpSession = getThreadLocalRequest().getSession();
		//HttpServletRequest request =  this.getThreadLocalRequest();
		 //String uid = "null";
		// System.out.println(RequestContextHolder.currentRequestAttributes());
		 //ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//		  if (requestAttributes != null) {
//		       HttpServletRequest req = requestAttributes.getRequest();
//		       uid = req.getSession().getAttribute("user").toString();
//		  }
		//String uid = (String) request.getSession().getAttribute("user");
		ZeusUser u= LoginUser.getUser();
		String uid = u.getUid();
		
		return uid;

	}

}
