package com.taobao.zeus.web.platform.server.rpc;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	public String checkUser(String username,String password) {
		//ZeusUser u= LoginUser.getUser();
		//HttpServletRequest httpRequest
		ZeusUser u = userManager.findByUid(username);
//		System.out.println(u);
		if(null == u){
			return "null";
		}else{
			String ps = u.getPassword();
//			System.out.println(password);
//			System.out.println(ps);
//			System.out.println(MD5(password));
			if(null !=ps){
				if(!MD5(password).toUpperCase().equals(ps.toUpperCase())){
					return "error";
				}
			}
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
	private static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            //System.out.println("MD5(" + sourceStr + ",32) = " + result);
            
            //System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }

	public String checkUserSession() {
		ZeusUser u= LoginUser.getUser();
		if(null==u){
			return "null";
		}
		String uid = u.getUid();
		
		return uid;

	}

}
