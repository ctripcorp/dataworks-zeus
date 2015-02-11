package com.taobao.zeus.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;





import com.taobao.zeus.broadcast.alarm.MailAlarm;
import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.store.mysql.persistence.ZeusUser.UserStatus;
import com.taobao.zeus.web.LoginUser;


public class RegisterPage  extends HttpServlet  {

	private static final long serialVersionUID = 1L;
	private UserManager userManager;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}
	
	@Override
	public void init(ServletConfig servletConfig ) throws ServletException {
		ApplicationContext applicationContext=WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		userManager=(UserManager) applicationContext.getBean("userManager");
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();     
        
		String uid = request.getParameter("user");
        ZeusUser u = userManager.findByUid(uid);
		if(null != u){
			out.print("exist");
		}else{
			try{
		        ZeusUser diAdmin = userManager.findByUid("diadmin");
		        String password = request.getParameter("passwd");
		        String email = request.getParameter("email");
		        String phone = request.getParameter("phone");
		        String userType = request.getParameter("userType");
				String passwordMD5 = MD5(password);
				String description = request.getParameter("description");
				ZeusUser newUser = new ZeusUser();
				newUser.setUid(uid);
				newUser.setName(uid);
				newUser.setEmail(email);
				newUser.setPhone(phone);
				newUser.setWangwang("");
				newUser.setPassword(passwordMD5);
				newUser.setGmtCreate(new Date());
				newUser.setGmtModified(new Date());
				newUser.setUserType(Integer.parseInt(userType));
				newUser.setDescription(description);
				ZeusUser returnUser = userManager.addOrUpdateUser(newUser);
				if(null != returnUser){
					List<String> mailUsers = new ArrayList<String>();
					mailUsers.add(ZeusUser.ADMIN.getUid());
					MailAlarm mailAlarm = new MailAlarm();
					List<String> emails = getEmailsByUsers(mailUsers);
					if(emails != null && emails.size()>0){
						emails.add(returnUser.getEmail());
						emails.add(diAdmin.getEmail());
						mailAlarm.sendEmail("", emails, "Zeus新用户注册申请",
								"Dear All,"+
								"\r\n	Zeus系统有新用户注册，详细信息如下："+
								"\r\n		用户类别："+(returnUser.getUserType()==0 ? "组用户" : "个人用户")+
								"\r\n		用户账号："+returnUser.getUid()+
								"\r\n		用户姓名："+returnUser.getName()+
								"\r\n		用户邮箱："+returnUser.getEmail()+
								"\r\n	请确认并审核。\r\n"+
								"\r\n	另外，请DI团队开通hive帐号及权限，权限描述如下："+
								"\r\n		" + returnUser.getDescription()+
								"\r\n	\r\n	\r\n谢谢！");
					}
					out.print(returnUser.getUid());
				}else{
					out.print("error");
				}
			}catch(Exception ex){
				out.print("error");
			}
		}
	}
	
	private List<String> getEmailsByUsers(List<String> users){
		List<String> emails = new ArrayList<String>();
		try{
			List<ZeusUser> userList = userManager.findListByUid(users);
			if (userList != null && userList.size() > 0) {
				for (ZeusUser user : userList) {
					String userEmail = user.getEmail();
					if (userEmail != null && !userEmail.isEmpty()
							&& userEmail.contains("@")) {
						if (userEmail.contains(";")) {
							String[] userEmails = userEmail.split(";");
							for (String ems : userEmails) {
								if (ems.contains("@")) {
									emails.add(ems);
								}
							}
						} else {
							emails.add(userEmail);
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return emails;
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

}
