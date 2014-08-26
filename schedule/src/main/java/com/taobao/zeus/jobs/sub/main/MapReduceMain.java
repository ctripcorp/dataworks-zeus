package com.taobao.zeus.jobs.sub.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class MapReduceMain {
	public static void main(String[] args) throws Exception{
		Class<?> main=Class.forName(args[0]);
		String workDir=System.getenv("instance.workDir");
		String jar=findContainingJar(main);
		Configuration conf=new Configuration(false);
		File f=new File(workDir+File.separator+"core-site.xml");
		if(f.exists()){
			conf.addResource(f.toURI().toURL());
		}else{
			File core=new File(System.getenv("HADOOP_HOME")+File.separator+"etc"+File.separator+"hadoop"+File.separator+"core-site.xml");
			if(core.exists()){
				conf.addResource(core.toURI().toURL());
			}
		}
		//conf.set("mapred.jar", jar);
		conf.set("mapreduce.job.jar", jar);
		File target=new File(workDir+File.separator+"hadoop_conf"+File.separator+"core-site.xml");
		if(!target.exists()){
			target.mkdirs();
		}
		conf.writeXml(new FileOutputStream(target));
		
		Method method=main.getDeclaredMethod("main", new Class[]{String[].class});
		String[] nargs=new String[args.length-1];
		for(int i=1;i<args.length;i++){
			nargs[i-1]=args[i];
		}
		method.invoke(null, new Object[]{nargs});
	}
	
	private static String findContainingJar(Class<?> my_class) {
	    ClassLoader loader = my_class.getClassLoader();
	    String class_file = my_class.getName().replaceAll("\\.", "/") + ".class";
	    try {
	      for(Enumeration<?> itr = loader.getResources(class_file);
	          itr.hasMoreElements();) {
	        URL url = (URL) itr.nextElement();
	        if ("jar".equals(url.getProtocol())) {
	          String toReturn = url.getPath();
	          if (toReturn.startsWith("file:")) {
	            toReturn = toReturn.substring("file:".length());
	          }
	          toReturn = URLDecoder.decode(toReturn, "UTF-8");
	          return toReturn.replaceAll("!.*$", "");
	        }
	      }
	    } catch (IOException e) {
	      throw new RuntimeException(e);
	    }
	    return null;
	  }
}
