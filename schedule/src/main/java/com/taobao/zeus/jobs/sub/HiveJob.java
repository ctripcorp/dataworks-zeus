package com.taobao.zeus.jobs.sub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.ProcessJob;
import com.taobao.zeus.model.FileDescriptor;
import com.taobao.zeus.store.FileManager;
import com.taobao.zeus.util.PropertyKeys;
import com.taobao.zeus.util.RunningJobKeys;

/**
 * 添加重试功能，如果任务在10分钟内失败，则进行重试
 * 
 * @author zhoufang
 * 
 */
public class HiveJob extends ProcessJob {
	private static Logger log=LoggerFactory.getLogger(HiveJob.class);
	public static final String UDF_SQL_NAME = "zeus_udf.sql";
	private FileManager fileManager;
	private ApplicationContext applicationContext;

	@SuppressWarnings("unused")
	public HiveJob(JobContext jobContext, ApplicationContext applicationContext) {
		super(jobContext);
		this.applicationContext = applicationContext;
		fileManager = (FileManager) this.applicationContext
				.getBean("fileManager");
		jobContext.getProperties().setProperty(RunningJobKeys.JOB_RUN_TYPE, "HiveJob");
		
	}

	@Override
	public Integer run() throws Exception {
		Date start = new Date();
		Integer exitCode = runInner();
		// 如果任务失败，且整个任务执行时间小于10分钟，则进行重试
		if (exitCode != 0
				&& getJobContext().getRunType() == JobContext.SCHEDULE_RUN
				&& new Date().getTime() - start.getTime() < 10 * 60 * 1000L) {
			log("Hive Job Fail in 10 min , try to retry");
			exitCode = runInner();
		}
		return exitCode;
	}


	
	public Integer runInner() throws Exception {
		

		String script = getProperties().getLocalProperty(PropertyKeys.JOB_SCRIPT);
		File f = new File(jobContext.getWorkDir() + File.separator
				+ (new Date().getTime()) + ".hive");
		if (!f.exists()) {
			f.createNewFile();
		}
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(f),
					Charset.forName(jobContext.getProperties().getProperty("zeus.fs.encode", "utf-8")));
			writer.write(script.replaceAll("^--.*", ""));
		} catch (Exception e) {
			jobContext.getJobHistory().getLog().appendZeusException(e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
		getProperties().setProperty(PropertyKeys.RUN_HIVE_PATH,f.getAbsolutePath());
		return super.run();
	}

	@Override
	public List<String> getCommandList() {
		String hiveFilePath = getProperty(PropertyKeys.RUN_HIVE_PATH, "");
		List<String> list = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		
		// get operator uid
		String shellPrefix = "";
		String user = "";
		if (jobContext.getRunType() == 1 || jobContext.getRunType() == 2) {
			user = jobContext.getJobHistory().getOperator();
			shellPrefix = "sudo -u " + user;
		} else if (jobContext.getRunType() == 3) {
			user = jobContext.getDebugHistory().getOwner();
			shellPrefix = "sudo -u " + user;
		} else if (jobContext.getRunType() == 4) {
			shellPrefix = "";
		}else{
			log("没有RunType=" + jobContext.getRunType() + " 的执行类别");
		}
		
		//格式转换
		list.add("dos2unix " + hiveFilePath);
		
		// 引入常用udf函数
		if (getUdfSql()) {
			sb.append(" -i ").append(jobContext.getWorkDir())
					.append(File.separator).append(UDF_SQL_NAME);
		}

		sb.append(" -f ").append(hiveFilePath);
		// 执行shell
		if(shellPrefix.trim().length() > 0){
			String envFilePath = this.getClass().getClassLoader().getResource("/").getPath()+"env.sh";
			String tmpFilePath = jobContext.getWorkDir()+File.separator+"tmp.sh";
			String localEnvFilePath = jobContext.getWorkDir()+File.separator+"env.sh";
			File f=new File(envFilePath);
			if(f.exists()){
				list.add("cp " + envFilePath + " " + jobContext.getWorkDir());
				File tmpFile = new File(tmpFilePath);
				OutputStreamWriter tmpWriter=null;
				try {
					if(!tmpFile.exists()){
						tmpFile.createNewFile();
					}
					tmpWriter=new OutputStreamWriter(new FileOutputStream(tmpFile),Charset.forName(jobContext.getProperties().getProperty("zeus.fs.encode", "utf-8")));
					tmpWriter.write("source " + localEnvFilePath + "; hive"+ sb.toString());
				} catch (Exception e) {
					jobContext.getJobHistory().getLog().appendZeusException(e);
				} finally{
					IOUtils.closeQuietly(tmpWriter);
				}
				list.add("chmod -R 755 " + jobContext.getWorkDir());
				list.add(shellPrefix + " sh " + tmpFilePath);
			}else{
				list.add("chmod -R 755 " + jobContext.getWorkDir());
				list.add(shellPrefix + " hive" + sb.toString());
			}
		}else{
			list.add("hive" + sb.toString());
		}
		return list;
	}

	@SuppressWarnings("unused")
	private boolean getUdfSql() {
		//TODO 请在此处填写udf文件对应的文档id
		String fileID=null;
		if(fileID==null){
			return false;
		}
		try {
			FileDescriptor file = fileManager.getFile(fileID);
			File f = new File(jobContext.getWorkDir() + File.separator
					+ UDF_SQL_NAME);
			if (f.exists()) {
				f.delete();
			}
			FileWriter fos = new FileWriter(f);
			fos.write(file.getContent());
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			log("获取同步表脚本失败");
			log(e);
			return false;
		}
	}
}
