package com.taobao.zeus.jobs.sub;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.conf.ConfUtil;
import com.taobao.zeus.jobs.sub.main.MapReduceMain;
import com.taobao.zeus.jobs.sub.tool.DownloadHdfsFileJob;
import com.taobao.zeus.store.HierarchyProperties;
import com.taobao.zeus.util.RunningJobKeys;

public class MapReduceJob extends JavaJob{

	
	
	public MapReduceJob(JobContext jobContext) {
		super(jobContext);
		String main=getJavaClass();
		String args=getMainArguments();
		String classpath=getClassPaths();
		jobContext.getProperties().setProperty(RunningJobKeys.RUN_JAVA_MAIN_CLASS, "com.taobao.zeus.jobs.sub.main.MapReduceMain");
		classpath=getMRClassPath(classpath);
		jobContext.getProperties().setProperty(RunningJobKeys.RUN_CLASSPATH, classpath+
				File.pathSeparator+getSourcePathFromClass(MapReduceMain.class));
		jobContext.getProperties().setProperty(RunningJobKeys.RUN_JAVA_MAIN_ARGS, main+" "+args);
		jobContext.getProperties().setProperty(RunningJobKeys.JOB_RUN_TYPE, "MapReduceJob");
		
	}
	
	//hadoop2依赖的JAR包，Apache需要的jar在${HADOOP_HOME}/libs/目录下，其他版本可能在${HADOOP_HOME}/lib
	public String getMRClassPath(String classpath){
		StringBuilder sb=new StringBuilder(classpath);
		String hadoophome=System.getenv("HADOOP_HOME");
		if(hadoophome!=null && !"".equals(hadoophome)){
			File f1=new File(hadoophome+"/libs");
			if(f1.exists()){
				sb.append(File.pathSeparator);
				sb.append(hadoophome);
				sb.append("/libs/*");	
			}
			File f2=new File(hadoophome+"/lib");
			if(f2.exists()){
				sb.append(File.pathSeparator);
				sb.append(hadoophome);
				sb.append("/lib/*");	
			}
		}
		return sb.toString();
	}
	
	@Override
	public Integer run() throws Exception {
		List<Map<String, String>> resources=jobContext.getResources();
		if(resources!=null && !resources.isEmpty()){
			StringBuffer sb=new StringBuffer();
			for(Map<String, String> map:jobContext.getResources()){
				if(map.get("uri")!=null){
					String uri=map.get("uri");
					if(uri.startsWith("hdfs://") && uri.endsWith(".jar")){
						sb.append(uri.substring("hdfs://".length())).append(",");
					}
				}
			}
			jobContext.getProperties().setProperty("core-site.tmpjars", sb.toString().substring(0, sb.toString().length()-1));
		}
		return super.run();
	}

	public static void main(String[] args) {
		JobContext context=JobContext.getTempJobContext();
		Map<String, String> map=new HashMap<String, String>();
		map.put("hadoop.ugi.name", "uginame");
		HierarchyProperties properties=new HierarchyProperties(map);
		context.setProperties(properties);
		
		new MapReduceJob(context);
	}

}
