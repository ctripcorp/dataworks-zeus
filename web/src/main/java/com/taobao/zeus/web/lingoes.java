package com.taobao.zeus.web;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/lingoes.do")
public class lingoes extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
		String url  = "/lingoes.html";
		request.getRequestDispatcher(url).forward(request,response);
    }

}