package com.taobao.zeus.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class YuntiJobDetailServlet extends HttpServlet{

	private static String JOBTRACKER;
	
	private static final long serialVersionUID = 1L;

    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	JOBTRACKER=config.getServletContext().getInitParameter("jobtracker");
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String type=req.getParameter("type");
		if("1".equals(type)){//内容
			fillContent(req, resp);
		}else if("2".equals(type)){//重定向detail
			redirectToJobTrackerDetail(req, resp);
		}else if("3".equals(type)){//重定向history
			redirectToJobTrackerHistory(req, resp);
		}
		
	}
	
	private void fillContent(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String job=req.getParameter("jobId");
		if(job==null){
			job=req.getParameter("jobid");
		}
		String str="http://"+JOBTRACKER+"/jobdetails.jsp?jobid="+job+"&refresh=30";
		URL url=new URL(str);
		HttpURLConnection httpUrlCon=(HttpURLConnection)url.openConnection();
		InputStreamReader inRead=new InputStreamReader(httpUrlCon.getInputStream(),"gbk");
		BufferedReader bufRead=new BufferedReader(inRead);
		StringBuffer strBuf=new StringBuffer();
		String line="";
		while((line=bufRead.readLine())!=null){
			strBuf.append(line);
		}
		String html=strBuf.toString();
		if(html.length()>0){
			String need=html.substring(html.indexOf("<table "),html.indexOf("<p/>"));
			need=need.replaceAll("href=\"", "target='_blank' href=\"http://"+JOBTRACKER+"/");
			need=need.replace("Kind", "<a target='_blank' href='"+str+"'>Job</a>");
			resp.getWriter().print("<html><head><title>Yunti Job Detail</title>" +
					"<link rel='stylesheet' type='text/css' href='http://"+JOBTRACKER+"/static/hadoop.css'></head><body style='margin:0px;overflow-y:hidden'>" +
					""+need+"</body></html>");
		}
	}
	
	private void redirectToJobTrackerDetail(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String job=req.getParameter("jobId");
		if(job==null){
			job=req.getParameter("jobid");
		}
		String str="http://"+JOBTRACKER+"/jobdetails.jsp?jobid="+job+"&refresh=30";
		resp.sendRedirect(str);
	}
	private void redirectToJobTrackerHistory(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String job=req.getParameter("jobId");
		if(job==null){
			job=req.getParameter("jobid");
		}
		String str="http://"+JOBTRACKER+"/jobhistory.jsp?jobid="+job+"&refresh=30";
		resp.sendRedirect(str);
	}
}
