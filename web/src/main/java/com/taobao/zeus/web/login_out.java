package com.taobao.zeus.web;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login_out.do")
public class login_out extends HttpServlet {
	
	@Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
		request.getSession().removeAttribute("user");
		Cookie cookie = new Cookie("LOGIN_USERNAME", null); 
		cookie.setMaxAge(0);
		response.addCookie(cookie); 
		response.sendRedirect("/zeus-web/login.do");
			
        }
	
}