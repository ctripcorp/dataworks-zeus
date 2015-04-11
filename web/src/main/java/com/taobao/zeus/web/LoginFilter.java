package com.taobao.zeus.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.web.LoginUser;
/**
 * 登陆信息设置
 * @author zhoufang
 *
 */
public class LoginFilter implements Filter {
	
	
	private UserManager userManager;
	private SSOLogin login=new SSOLogin() {
		public String getUid(HttpServletRequest req) {
		
			return ZeusUser.USER.getUid();
		}
		public String getPhone(HttpServletRequest req) {
			return ZeusUser.USER.getPhone();
		}
		public String getName(HttpServletRequest req) {
			return ZeusUser.USER.getName();
		}
		public String getEmail(HttpServletRequest req) {
			return ZeusUser.USER.getEmail();
		}
	};
	@Override
	public void destroy() {
		// do nothing
	}
	
	
	public interface SSOLogin{
		String getUid(HttpServletRequest req);
		String getEmail(HttpServletRequest req);
		String getName(HttpServletRequest req);
		String getPhone(HttpServletRequest req);
	}
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest=(HttpServletRequest) request;
		HttpServletResponse httpResponse=(HttpServletResponse)response;
		httpResponse.setCharacterEncoding("utf-8");
		String uri=httpRequest.getRequestURI();
		//线上服务器检测

		if(uri.equals("/zeus.check")){
			response.getWriter().write("success");
			return;
		}
		
		ZeusUser zeusUser=null;

		if(uri.endsWith(".taobao") || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".gif") ||
				uri.endsWith(".jpg") || uri.endsWith(".png") || uri.endsWith(".do")|| uri.endsWith(".jsp")|| 
				uri.endsWith("login.html")|| uri.endsWith("userInfo.html")|| uri.endsWith("userEdit.html")|| 
				uri.endsWith(".ico")){
			chain.doFilter(request, response);
			return;
		}
		
		String uid=(String) httpRequest.getSession().getAttribute("user");//login.getUid(httpRequest);
//		System.out.println("getSession-----------"+uid);
		if(null!=uid){//如果存在session
			Boolean check = false;
			Cookie[] cookies = httpRequest.getCookies();
            for(Cookie c :cookies ){
            	if(c.getName().equals("LOGIN_USERNAME")){
            		if(c.getValue().equals(uid)){
            			zeusUser=new ZeusUser();
            			zeusUser.setUid(uid);
            			check = true;
            			//userManager.addOrUpdateUser(zeusUser);
            			LoginUser.user.set(zeusUser);
            		}
            	}
 
            }
           if(!check){
        	   httpResponse.sendRedirect("/zeus-web/login.do");
        	   return;  
           }
			
		}else{//不存在user
			 httpResponse.sendRedirect("/zeus-web/login.do");
			 return;  
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext applicationContext=WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		userManager=(UserManager) applicationContext.getBean("userManager");
		if(applicationContext.containsBean("ssoLogin")){
			login=(SSOLogin)applicationContext.getBean("ssologin");
		}
	}

}
