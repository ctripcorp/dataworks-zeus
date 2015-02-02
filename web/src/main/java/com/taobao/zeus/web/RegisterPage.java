package com.taobao.zeus.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;



import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
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
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();     
        
		String uid = request.getParameter("user");
        ZeusUser u = userManager.findByUid(uid);

		if(null != u){
			out.print("exist");
		}else{
			try{
		        String password = request.getParameter("passwd");
		        String email = request.getParameter("email");
		        String phone = request.getParameter("phone");
		        String userType = request.getParameter("userType");
				String passwordMD5 = MD5(password);
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
				ZeusUser returnUser = userManager.addOrUpdateUser(newUser);
				if(null != returnUser){
					out.print(returnUser.getUid());
				}else{
					out.print("error");
				}
			}catch(Exception ex){
				out.print("error");
			}
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

}
