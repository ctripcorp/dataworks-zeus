package com.taobao.zeus.jobs;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.taobao.zeus.model.FileDescriptor;
import com.taobao.zeus.store.FileManager;

public class WithProcesserJob extends AbstractJob{

	private List<Job> pres;
	private List<Job> posts;
	private Job job;
	private FileManager fileManager;
	public WithProcesserJob(JobContext jobContext,
			List<Job> pres,List<Job> posts,Job job,ApplicationContext applicationContext) {
		super(jobContext);
		this.pres=pres;
		this.posts=posts;
		this.job=job;
		this.fileManager = (FileManager) applicationContext.getBean("fileManager");
	}
	
	private Job running;
	
	@Override
	public Integer run() throws Exception {
		String jobId=null;
		String historyId=null;
		boolean isDebug=false;
		FileDescriptor fd=null;
		if(jobContext.getDebugHistory()!=null){
			isDebug = true;
			fd = fileManager.getFile(jobContext.getDebugHistory().getFileId());
		}else {
			jobId=jobContext.getJobHistory().getJobId();
			historyId = jobContext.getJobHistory().getId();
		}
		//前置任务执行
		Integer preExitCode=-1;
		for(Job job:pres){
			if(isCanceled()){
				break;
			}
			try {
				running=job;
				log("开始执行前置处理单元："+job.getClass().getSimpleName());
				preExitCode=job.run();
				jobContext.setPreExitCode(preExitCode);
			} catch (Exception e) {
				jobContext.setPreExitCode(-1);
				log(e);
			} finally{
				log("前置处理单元："+job.getClass().getSimpleName()+" 处理完毕");
				running=null;
			}
		}
		//核心任务执行
		Integer exitCode=-1;
		jobContext.setCoreExitCode(exitCode);
		try {
			if(!isCanceled()){
				log("开始执行核心Job任务");
				running=job;
				exitCode=job.run();
				jobContext.setCoreExitCode(exitCode);
			}
		} catch (Exception e) {
			jobContext.setCoreExitCode(exitCode);
			log(e);
		} finally{
			log("核心Job任务处理完毕");
			running=null;
		}
		//后置任务执行
		Integer postExitCode=-1;
		for(Job job:posts){
			if(isCanceled()){
				break;
			}
			try {
				log("开始执行后置处理单元："+job.getClass().getSimpleName());
				running=job;
				postExitCode=job.run();
				jobContext.setPreExitCode(postExitCode);
			} catch (Exception e) {
				jobContext.setPreExitCode(postExitCode);
				log(e);
			} finally{
				log("后置处理单元："+job.getClass().getSimpleName()+"处理完毕");
				running=null;
			}
		}
		
		return exitCode;
	}

	@Override
	public void cancel() {
		log("开始执行取消任务命令");
		canceled=true;
		if(running!=null){
			running.cancel();
		}
		log("结束取消任务命令");
	}
}
