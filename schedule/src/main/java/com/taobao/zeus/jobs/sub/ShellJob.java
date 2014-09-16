package com.taobao.zeus.jobs.sub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.ProcessJob;
import com.taobao.zeus.store.Super;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.util.PropertyKeys;
import com.taobao.zeus.web.LoginUser;
import com.taobao.zeus.web.platform.client.util.ZUser;
/**
 * 采用Shell脚本的任务
 * @author zhoufang
 *
 */
public class ShellJob extends ProcessJob{

	protected String shell;

	public ShellJob(JobContext jobContext) {
		super(jobContext);
	}
	public ShellJob(JobContext jobContext,String shell){
		this(jobContext);
		this.shell=shell;
	}
	
	@Override
	public Integer run() throws Exception {
		return super.run();
	}

	@Override
	public List<String> getCommandList() {
		String script=null;
		if(shell!=null){
			script=shell;
		}else{
			script=getProperties().getLocalProperty(PropertyKeys.JOB_SCRIPT);
		}
		
		OutputStreamWriter writer=null;
		try {
			File f=new File(jobContext.getWorkDir()+File.separator+(new Date().getTime())+".sh");
			if(!f.exists()){
				f.createNewFile();
			}
			writer=new OutputStreamWriter(new FileOutputStream(f),Charset.forName(jobContext.getProperties().getProperty("zeus.fs.encode", "utf-8")));
			writer.write(script);
			getProperties().setProperty(PropertyKeys.RUN_SHELLPATH, f.getAbsolutePath());
		} catch (Exception e) {
			jobContext.getJobHistory().getLog().appendZeusException(e);
		} finally{
			IOUtils.closeQuietly(writer);
		}
		
		String shellFilePath=getProperty(PropertyKeys.RUN_SHELLPATH, "");
		
		List<String> list=new ArrayList<String>();
		
		// get current username
		ZeusUser u= LoginUser.getUser();
		String shellUserName = u.getName();
		
		//修改权限
		list.add("chmod u+x " + shellFilePath);
		//格式转换
		list.add("dos2unix " + shellFilePath);
		//执行shell
		// run shell as current user
		list.add("sudo -u " + shellUserName + " sh "+shellFilePath);
		return list;
	}
}
