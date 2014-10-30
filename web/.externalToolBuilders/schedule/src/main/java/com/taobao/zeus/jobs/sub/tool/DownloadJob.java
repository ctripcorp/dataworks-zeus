package com.taobao.zeus.jobs.sub.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.Job;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.RenderHierarchyProperties;
import com.taobao.zeus.jobs.sub.conf.ConfUtil;

/**
 * 下载文件的Job
 * 
 * @author zhoufang
 * 
 */
public class DownloadJob extends AbstractJob {

	public DownloadJob(JobContext jobContext) {
		super(jobContext);
	}

	@Override
	public Integer run() throws IOException {
		List<Job> jobs = new ArrayList<Job>();
		for (Map<String, String> map : jobContext.getResources()) {
			if (map.get("uri") != null) {
				String name = map.get("name");
				String uri = map.get("uri");
				if (uri.startsWith("hdfs://")) {
					jobs.add(new DownloadHdfsFileJob(jobContext, jobContext
							.getWorkDir() + File.separator + name, uri
							.substring(7)));
				} else if (uri.startsWith("doc://")) {
					String fileId = uri.substring(uri.lastIndexOf('/')+1);
					String script = map.get("zeus-doc-"+fileId);
					script = RenderHierarchyProperties.render(script);
					File f = new File(jobContext
							.getWorkDir() + File.separator + name);
					if(f.exists()){
						log( name +"已存在，可能是重名或循环引用");
						continue;
					}
					FileWriter w=null;
					try {
						w = new FileWriter(f);
						w.write(script);
						w.flush();
					} catch (Exception e) {
						log(e);
					} finally{
						if(w!=null){
							w.close();
						}
					}
				} else if (uri.startsWith("http://")){
					if(name==null||name.trim().isEmpty()){
						log("download from http error! name not specified!");
						continue;
					}else if(!name.endsWith(".xml") && !name.endsWith(".txt")){
						log(name+" is not allow to download");
						continue;
					}
					HttpClient client = new HttpClient();
					GetMethod getMethod = new GetMethod(uri);
					int statusCode = client.executeMethod(getMethod);
					if (statusCode != HttpStatus.SC_OK) {
					  log("download from http error! code="+statusCode);
					}else{
						BufferedReader in=null;
						try{
							in=new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(),getMethod.getRequestCharSet()));
							StringBuffer sb=new StringBuffer();
							String inputLine=null;
							while((inputLine=in.readLine())!=null){
								sb.append(inputLine);
							}
							String script = sb.toString();
							if(script==null||script.trim().isEmpty()){
								log( name +"为空");
								continue;
							}
							script = RenderHierarchyProperties.render(script);
							File f = new File(jobContext
									.getWorkDir() + File.separator + name);
							if(f.exists()){
								log( name +"已存在，可能是重名或循环引用");
								continue;
							}
							FileWriter w=null;
							try {
								w = new FileWriter(f);
								w.write(script);
								w.flush();
							} catch (Exception e) {
								log(e);
							} finally{
								if(w!=null){
									w.close();
								}
							}
						}catch (Exception e) {
							log(e);
						}finally{
							if(in!=null){
								in.close();
							}
						}
					}
				}
			}
		}

		Integer exitCode = 0;
		for (Job job : jobs) {
			try {
				exitCode = job.run();
			} catch (Exception e) {
				jobContext.getJobHistory().getLog().appendZeusException(e);
			}
		}
		return exitCode;
	}

	@Override
	public void cancel() {
		canceled = true;
	}

}
