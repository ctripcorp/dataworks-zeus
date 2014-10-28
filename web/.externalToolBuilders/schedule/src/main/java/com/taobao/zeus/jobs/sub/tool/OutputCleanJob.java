package com.taobao.zeus.jobs.sub.tool;


import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.conf.ConfUtil;
import com.taobao.zeus.model.processer.OutputCleanProcesser;

public class OutputCleanJob extends AbstractJob{

	private OutputCleanProcesser ocp;
	public OutputCleanJob(JobContext jobContext,OutputCleanProcesser p,ApplicationContext applicationContext) {
		super(jobContext);
		this.ocp=p;
	}
	@Override
	public Integer run() throws Exception {
		if(jobContext.getCoreExitCode()!=0){
			log("Job 运行失败，不进行产出目录清理");
			return 0;
		}
		log("OutputClean 开始进行产出目录清理");
		String path=ocp.getPath();
		FileSystem fs=FileSystem.get(ConfUtil.getDefaultCoreSite());
		FileStatus[] pathFiles=fs.listStatus(new Path(path));
		boolean valid=true;
		for(FileStatus f:pathFiles){
			if(f.isDir()){
				valid=false;
				log("产出路径下面有文件夹，怀疑路径设置有错，拒绝执行清理操作");
				break;
			}
		}
		if(!valid){
			return 0;
		}
		String upperPath=path;
		if(upperPath.endsWith("/")){
			upperPath=upperPath.substring(0,path.length()-1);
		}
		upperPath=upperPath.substring(0,upperPath.lastIndexOf("/"));
		Path hdfsPath=new Path(upperPath);
		
		SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, ocp.getDays()*(-1));
		Date limit=format.parse(format.format(cal.getTime()));
		FileStatus[] files=fs.listStatus(hdfsPath);
		for(FileStatus f:files){
			String tmpPath=f.getPath().toString();
			if(tmpPath.contains("/pt=")){
				String yyyyMMdd=tmpPath.substring(tmpPath.indexOf("/pt=")+4,tmpPath.indexOf("/pt=")+12);
				Date fdate=format.parse(yyyyMMdd);
				if(fdate.before(limit)){
					jobContext.getJobHistory().getLog().appendZeus("删除目录：" +tmpPath);
					fs.delete(f.getPath(),true);
				}
			}
		}
		return 0;
	}
	@Override
	public void cancel() {
		canceled=true;
	}
	
}
