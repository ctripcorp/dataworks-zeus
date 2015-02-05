package com.taobao.zeus.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.zeus.broadcast.alarm.MailAlarm;
import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.store.mysql.persistence.ZeusUser.UserStatus;

public class UserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private UserManager userManager;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(servletConfig.getServletContext());
		userManager = (UserManager) applicationContext.getBean("userManager");
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		Object sessionUser = request.getSession().getAttribute("user");
		if (sessionUser == null) {
			response.sendRedirect(request.getContextPath() + "/login.do");
			return;
		}

		String action = request.getParameter("action");
		System.out.println("action=" + action);
		if (action != null) {
			if ("list".equals(action)) {
				try{
					int pageIndex = Integer.parseInt(request.getParameter(
							"pageIndex").toString());
					int pageSize = Integer.parseInt(request
							.getParameter("pageSize").toString());
					String sortField = request.getParameter("sortField");
					String sortOrder = request.getParameter("sortOrder");
					String filter = request.getParameter("key");
					List<ZeusUser> allUsers = new ArrayList<ZeusUser>();
					System.out.println(sessionUser.toString());
					System.out.println("Admin user:" + ZeusUser.ADMIN.getUid());
	
					if (sessionUser.toString().equalsIgnoreCase(
							ZeusUser.ADMIN.getUid())) {
						if (filter != null && filter.trim().length() > 0) {
							allUsers = userManager.findListByFilter(filter,
									sortField, sortOrder);
						} else {
							allUsers = userManager.findAllUsers(sortField,
									sortOrder);
						}
					} else {
						ZeusUser user = userManager.findByUid(sessionUser.toString());
						allUsers.add(user);
					}
					if (allUsers.size() > 0) {
						// 实现一个内存分页(实际应该使用SQL分页)
						List<ZeusUser> pageUsers = new ArrayList<ZeusUser>();
						int start = pageIndex * pageSize, end = start + pageSize;
	
						for (int i = 0, l = allUsers.size(); i < l; i++) {
							ZeusUser record = allUsers.get(i);
							if (record == null)
								continue;
							if (start <= i && i < end) {
								pageUsers.add(record);
							}
						}
						Map<String, Object> results = new HashMap<String, Object>();
						results.put("data", pageUsers);
						results.put("total", allUsers.size());
						JsonConfig jsonConfig = new JsonConfig();
						jsonConfig.registerJsonValueProcessor(Date.class,
								new JsonDateValueProcessor());
						JSONArray json = JSONArray.fromObject(results, jsonConfig);
						String jsonString = json.toString();
						System.out.println(jsonString);
						out.print(jsonString.substring(1, jsonString.length() - 1));
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			} else if ("add".equals(action)) {
				String data = request.getParameter("data");
				if (data != null && data.length() > 2) {
					try {
						JSONObject obj = JSONObject.fromObject(data.substring(
								1, data.length() - 1));
						String uid = getobjvalue(obj, "uid");
						ZeusUser user = userManager.findByUid(uid);
						if (user != null) {
							out.print("error");
						} else {
							user = new ZeusUser();
							user.setUid(uid);
							user.setPassword("123");
							user.setName(getobjvalue(obj, "name"));
							user.setEmail(getobjvalue(obj, "email"));
							user.setPhone(getobjvalue(obj, "phone"));
							user.setWangwang("");
							user.setDescription(getobjvalue(obj, "description"));
							user.setIsEffective(Integer.parseInt(getobjvalue(
									obj, "isEffective")));
							user.setUserType(Integer.parseInt(getobjvalue(obj,
									"userType")));
							user.setGmtCreate(new Date());
							user.setGmtModified(new Date());
							userManager.addOrUpdateUser(user);
							out.print("success");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			} else if ("get".equals(action)) {
				String uid = request.getParameter("uid");
				if (uid != null && uid.length() > 0) {
					try {
						ZeusUser user = userManager.findByUid(uid);
						if (user != null) {
							JsonConfig jsonConfig = new JsonConfig();
							jsonConfig.registerJsonValueProcessor(Date.class,
									new JsonDateValueProcessor());
							JSONObject json = JSONObject.fromObject(user, jsonConfig);
							String jsonString = json.toString();
							System.out.println(jsonString);
							out.print(jsonString);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}else{
					out.print("notExist");
				}
			} else if ("edit".equals(action)) {
				String data = request.getParameter("data");
				if (data != null && data.length() > 2) {
					try {
						JSONObject obj = JSONObject.fromObject(data.substring(
								1, data.length() - 1));
						String uid = getobjvalue(obj, "uid");
						ZeusUser user = userManager.findByUid(uid);
						if (user != null) {
							user.setUid(uid);
							user.setName(getobjvalue(obj, "name"));
							user.setEmail(getobjvalue(obj, "email"));
							user.setPhone(getobjvalue(obj, "phone"));
							user.setWangwang("");
							user.setDescription(getobjvalue(obj, "description"));
							user.setIsEffective(Integer.parseInt(getobjvalue(
									obj, "isEffective")));
							user.setUserType(Integer.parseInt(getobjvalue(obj,
									"userType")));
							user.setGmtModified(new Date());
							userManager.addOrUpdateUser(user);
							out.print("success");
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else if ("cancel".equals(action) || "checksuccess".equals(action) || "checkfailed".equals(action)) {
				String uids = request.getParameter("uids");
				int isEffective = UserStatus.WAIT_CHECK.value();
				if("cancel".equals(action)){
					isEffective = UserStatus.Cancel.value();
				}else if("checksuccess".equals(action)){
					isEffective = UserStatus.CHECK_SUCCESS.value();
				}else if("checkfailed".equals(action)){
					isEffective = UserStatus.CHECK_FAILED.value();
				}
				if (uids != null && uids.trim().length() > 0) {
					try{
						System.out.println(uids);
						for (String uid : uids.split(",")) {
							ZeusUser user = userManager.findByUid(uid);
							user.setIsEffective(isEffective);
							ZeusUser newUser = userManager.addOrUpdateUser(user);
							if("checksuccess".equals(action) && newUser.getIsEffective()==UserStatus.CHECK_SUCCESS.value()){
								List<String> mailUsers = new ArrayList<String>();
								mailUsers.add(ZeusUser.ADMIN.getUid());
								mailUsers.add(newUser.getUid());
								MailAlarm mailAlarm = new MailAlarm();
								List<String> emails = getEmailsByUsers(mailUsers);
								if(emails != null && emails.size()>0){
									emails.add("yongchengchen@Ctrip.com");
//									emails.add("jianguo@Ctrip.com");
//									emails.add("yafengli@Ctrip.com");
									mailAlarm.sendEmail("", emails, "Zeus新用户审核已通过",
											"Dear All,"+
											"\r\n	Zeus新用户审核已通过，详细信息如下："+
											"\r\n		用户类别："+(newUser.getUserType()==0 ? "组用户" : "个人用户")+
											"\r\n		用户账号："+newUser.getUid()+
											"\r\n		用户姓名："+newUser.getName()+
											"\r\n		用户邮箱："+newUser.getEmail()+
											"\r\n	请确认，另外请开通Hive账号和权限。谢谢！ "+
											"\r\n	权限描述如下："+
											"\r\n		" + newUser.getDescription());
								}
							}
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
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
	
	private String getobjvalue(JSONObject obj, String key) {
		if (obj.has(key)) {
			return obj.getString(key);
		}
		return "";
	}

}
