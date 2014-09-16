package com.taobao.zeus.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.zeus.jobs.sub.conf.ConfUtil;
import com.taobao.zeus.jobs.sub.tool.CancelHadoopJob;
import com.taobao.zeus.store.HierarchyProperties;
import com.taobao.zeus.util.RunningJobKeys;
/**
 * 通过操作系统创建进程Process的Job任务
 * @author zhoufang
 *
 */
public abstract class ProcessJob extends AbstractJob implements Job {
	
	private static Logger log=LoggerFactory.getLogger(ProcessJob.class);
	
	protected volatile Process process;
	
	protected final Map<String, String> envMap;
	
	public static final int CLEAN_UP_TIME_MS = 1000;
	
	public ProcessJob(JobContext jobContext){
		super(jobContext);
		envMap=new HashMap<String, String>(System.getenv());
	}

	
	public abstract List<String> getCommandList();
	
	
	private void buildHadoopConf(String jobType){
		File dir=new File(jobContext.getWorkDir()+File.separator+"hadoop_conf");
		if(!dir.exists()){
			dir.mkdirs();
		}
		Map<String, String> core=new HashMap<String, String>();
		Map<String, String> hdfs=new HashMap<String, String>();
		Map<String, String> mapred=new HashMap<String, String>();
		Map<String, String> yarn=new HashMap<String, String>();
		for(String key:jobContext.getProperties().getAllProperties().keySet()){
			if(key.startsWith("core-site.")){
				core.put(key.substring("core-site.".length()), jobContext.getProperties().getProperty(key));
			}else if(key.startsWith("hdfs-site.")){
				hdfs.put(key.substring("hdfs-site.".length()), jobContext.getProperties().getProperty(key));
			}else if(key.startsWith("mapred-site.")){
				mapred.put(key.substring("mapred-site.".length()), jobContext.getProperties().getProperty(key));
			}
			else if(key.startsWith("yarn-site.")){
				yarn.put(key.substring("yarn-site.".length()), jobContext.getProperties().getProperty(key));
			}
		}
		if(jobType!=null&&(jobType.equals("MapReduceJob")||jobType.equals("HiveJob")))
		{
			Configuration coreC=ConfUtil.getDefaultCoreSite();
			for(String key:core.keySet()){
				coreC.set(key, core.get(key));
			}
			try {
				File xml=new File(dir.getAbsolutePath()+File.separator+"core-site.xml");
				if(xml.exists()){
					xml.delete();
				}
				xml.createNewFile();
				coreC.writeXml(new FileOutputStream(xml));
			} catch (Exception e) 
			{
				log.error("create file core-site.xml error",e);
			}

			Configuration hdfsC=ConfUtil.getDefaultHdfsSite();
			for(String key:hdfs.keySet()){
				hdfsC.set(key, hdfs.get(key));
			}
			try {
				File xml=new File(dir.getAbsolutePath()+File.separator+"hdfs-site.xml");
				if(xml.exists()){
					xml.delete();
				}
				xml.createNewFile();
				hdfsC.writeXml(new FileOutputStream(xml));
			} catch (Exception e) {
				log.error("create file hdfs-site.xml error",e);
			}
		

			Configuration mapredC=ConfUtil.getDefaultMapredSite();
			for(String key:mapred.keySet()){
				mapredC.set(key, mapred.get(key));
			}
			try {
				File xml=new File(dir.getAbsolutePath()+File.separator+"mapred-site.xml");
				if(xml.exists()){
					xml.delete();
				}
				xml.createNewFile();
				mapredC.writeXml(new FileOutputStream(xml));
			} catch (Exception e) {
				log.error("create file mapred-site.xml error",e);
			}
			Configuration yarnC=ConfUtil.getDefaultYarnSite();
			for(String key:yarn.keySet()){
				yarnC.set(key, mapred.get(key));
			}
			try {
				File xml=new File(dir.getAbsolutePath()+File.separator+"yarn-site.xml");
				if(xml.exists()){
					xml.delete();
				}
				xml.createNewFile();
				yarnC.writeXml(new FileOutputStream(xml));
			} catch (Exception e) {
				log.error("create file yarn-site.xml error",e);
			}
		}
		
		//HADOOP_CONF_DIR添加2个路径，分别为 WorkDir/hadoop_conf 和 HADOOP_HOME/conf 
		String HADOOP_CONF_DIR=jobContext.getWorkDir()+File.separator+"hadoop_conf"+File.pathSeparator
				+ConfUtil.getHadoopConfDir();
		envMap.put("HADOOP_CONF_DIR", HADOOP_CONF_DIR);
	}
	private void buildHiveConf(String jobType){
		File dir=new File(jobContext.getWorkDir()+File.separator+"hive_conf");
		if(!dir.exists()){
			dir.mkdirs();
		}
		Map<String, String> hive=new HashMap<String, String>();
		for(String key:jobContext.getProperties().getAllProperties().keySet()){
			if(key.startsWith("hive-site.")){
				hive.put(key.substring("hive-site.".length()), jobContext.getProperties().getProperty(key));
			}
		}
		if(jobType!=null&&jobType.equals("HiveJob")){
			Configuration hiveC=ConfUtil.getDefaultHiveSite();
			for(String key:hive.keySet()){
				hiveC.set(key, hive.get(key));
			}
			try {
				File xml=new File(dir.getAbsolutePath()+File.separator+"hive-site.xml");
				if(xml.exists()){
					xml.delete();
				}
				xml.createNewFile();
				hiveC.writeXml(new FileOutputStream(xml));
			} catch (Exception e) {
				log.error("create file hive-site.xml error",e);
			}
		}
		
		
		String HIVE_CONF_DIR=jobContext.getWorkDir()+File.separator+"hive_conf"+File.pathSeparator+
				ConfUtil.getHiveConfDir();
		envMap.put("HIVE_CONF_DIR", HIVE_CONF_DIR);
	}
	
	public Integer run() throws Exception{
		
		int exitCode=-999;
		String jobType=jobContext.getProperties().getAllProperties().get(RunningJobKeys.JOB_RUN_TYPE);
		
		buildHadoopConf(jobType);
		buildHiveConf(jobType);
		
		//设置环境变量
		for(String key:jobContext.getProperties().getAllProperties().keySet()){
			if(jobContext.getProperties().getProperty(key)!=null && (key.startsWith("instance.") || key.startsWith("secret."))){
				envMap.put(key, jobContext.getProperties().getProperty(key));
			}
		}
		
		envMap.put("instance.workDir", jobContext.getWorkDir());
		
		List<String> commands=getCommandList();
		for(String s:commands){
			log("DEBUG Command:"+s);
			
			ProcessBuilder builder = new ProcessBuilder(partitionCommandLine(s));
			builder.directory(new File(jobContext.getWorkDir()));
			builder.environment().putAll(envMap);
			process=builder.start();
			final InputStream inputStream = process.getInputStream();
            final InputStream errorStream = process.getErrorStream();
			
			String threadName=null;
			if(jobContext.getJobHistory()!=null && jobContext.getJobHistory().getJobId()!=null){
				threadName="jobId="+jobContext.getJobHistory().getJobId();
			}else if(jobContext.getDebugHistory()!=null && jobContext.getDebugHistory().getId()!=null){
				threadName="debugId="+jobContext.getDebugHistory().getId();
			}else{
				threadName="not-normal-job";
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
						String line;
						while((line=reader.readLine())!=null){
							logConsole(line);
						}
					}catch(Exception e){
						log(e);
						log("接收日志出错，推出日志接收");
					}
				}
			},threadName).start();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BufferedReader reader=new BufferedReader(new InputStreamReader(errorStream));
						String line;
						while((line=reader.readLine())!=null){
								logConsole(line);
							
						}
					} catch (Exception e) {
							log(e);
							log("接收日志出错，推出日志接收");
						}
				}
			},threadName).start();
			
			exitCode = -999;
			try {
				exitCode = process.waitFor();
			} catch (InterruptedException e) {
				log(e);
			} finally{
				process=null;
			}
			if(exitCode!=0){
				return exitCode;
			}
		}
		return exitCode;
	}
	
	public void cancel(){
		try {
			new CancelHadoopJob(jobContext).run();
		} catch (Exception e1) {
			log(e1);
		}
		//强制kill 进程
		if (process != null) {
			log("WARN Attempting to kill the process ");
			try {
				process.destroy();
				int pid=getProcessId();
				Runtime.getRuntime().exec("kill -9 "+pid);
			} catch (Exception e) {
				log(e);
			} finally{
				process=null;
			}
		}
	}
	private int getProcessId() {
		int processId = 0;

		try {
			Field f = process.getClass().getDeclaredField("pid");
			f.setAccessible(true);

			processId = f.getInt(process);
		} catch (Throwable e) {
		}

		return processId;
	}

	
	protected String getProperty(String key,String defaultValue){
		String value=jobContext.getProperties().getProperty(key);
		if(value==null){
			value=defaultValue;
		}
		return value;
	}
	/**
	 * Splits the command into a unix like command line structure. Quotes and
	 * single quotes are treated as nested strings.
	 * 
	 * @param command
	 * @return
	 */
	public static String[] partitionCommandLine(String command) {
		
		ArrayList<String> commands = new ArrayList<String>();
		
		String os=System.getProperties().getProperty("os.name");
		if(os!=null && (os.startsWith("win") || os.startsWith("Win"))){
			commands.add("CMD.EXE");
			commands.add("/C");
			commands.add(command);
		}else{
//			commands.add("sudo -u root bash");
//			commands.add("su - biubt");
			int index = 0;

	        StringBuffer buffer = new StringBuffer(command.length());

	        boolean isApos = false;
	        boolean isQuote = false;
	        while(index < command.length()) {
	            char c = command.charAt(index);

	            switch(c) {
	                case ' ':
	                    if(!isQuote && !isApos) {
	                        String arg = buffer.toString();
	                        buffer = new StringBuffer(command.length() - index);
	                        if(arg.length() > 0) {
	                            commands.add(arg);
	                        }
	                    } else {
	                        buffer.append(c);
	                    }
	                    break;
	                case '\'':
	                    if(!isQuote) {
	                        isApos = !isApos;
	                    } else {
	                        buffer.append(c);
	                    }
	                    break;
	                case '"':
	                    if(!isApos) {
	                        isQuote = !isQuote;
	                    } else {
	                        buffer.append(c);
	                    }
	                    break;
	                default:
	                    buffer.append(c);
	            }

	            index++;
	        }

	        if(buffer.length() > 0) {
	            String arg = buffer.toString();
	            commands.add(arg);
	        }
		}
        return commands.toArray(new String[commands.size()]);
	}

	public HierarchyProperties getProperties(){
		return jobContext.getProperties();
	}
	public JobContext getJobContext() {
		return jobContext;
	}
	
}
