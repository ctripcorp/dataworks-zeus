package com.taobao.zeus.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.tool.UploadHdfsFileJob;
import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.store.HierarchyProperties;

public class FileUploadServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private static Logger log=LogManager.getLogger(FileUploadServlet.class);

	public static volatile String hdfsLibPath;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		hdfsLibPath=config.getServletContext().getInitParameter("hdfsLibPath");
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=utf-8");
		DiskFileItemFactory _uploadItemFactory;
		_uploadItemFactory = new DiskFileItemFactory();
		_uploadItemFactory.setSizeThreshold(1024*1024);
		ServletFileUpload upload = new ServletFileUpload(_uploadItemFactory);
		//100m
		upload.setSizeMax(100000000);
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(req);
			for (FileItem item : items) {
				// FileItemStream item = iterator.next();
				String name = item.getName();
				if(name.lastIndexOf("\\")!=-1){
					name=name.substring(name.lastIndexOf("\\")+1);
				}
				name=name.replace(' ', '_');
				// InputStream val = item.openStream();
				if (item.isFormField()) {
					continue;
				}
				
				OutputStream out = null;
				
				File temp=null;
				int index=name.lastIndexOf(".");
				String suffix="unk";
				if(index!=-1){
					suffix=name.substring(index+1);
				}
				try {
					temp=new File("/tmp/zeus/"+name+"-"+new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())+"."+suffix);
					temp.createNewFile();
					temp.deleteOnExit();
					out = new BufferedOutputStream(new FileOutputStream(temp));
					IOUtils.copy(item.getInputStream(), out);
				} catch (Exception e) {
					throw new ZeusException(e);
				} finally{
					IOUtils.closeQuietly(out);
				}
				
				JobHistory history=new JobHistory();
				JobContext jobContext=new JobContext();
				jobContext.setWorkDir(temp.getParent());
				jobContext.setJobHistory(history);
				jobContext.setProperties(new HierarchyProperties(new HashMap<String, String>()));
				UploadHdfsFileJob job=new UploadHdfsFileJob(jobContext, temp.getAbsolutePath(), hdfsLibPath);
				Integer exitCode=job.run();
				if(exitCode!=0){
					log.error(history.getLog().getContent());
					resp.getWriter().write(history.getLog().getContent());
				}else{
					resp.getWriter().write("[[uri=hdfs://"+hdfsLibPath+File.separator+temp.getName()+"]]");
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
