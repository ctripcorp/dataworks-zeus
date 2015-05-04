package com.taobao.zeus.jobs.sub.tool;

import java.util.Date;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;
import com.taobao.zeus.socket.worker.WorkerContext;
import com.taobao.zeus.util.Environment;
import com.taobao.zeus.util.RunShell;

public class CpuLoadPerCoreJob {
	private WorkerContext workerContext;
	private JobContext jobContext;
	private static Date date=new Date();
	public CpuLoadPerCoreJob(WorkerContext workerContext, JobContext jobContext){
		this.workerContext = workerContext;
		this.jobContext = jobContext;
	}
	public Integer run() throws Exception {
		//window mac 系统直接返回成功
		String os=System.getProperties().getProperty("os.name");
		if(os!=null && (os.startsWith("win") || os.startsWith("Win") || os.startsWith("Mac"))){
			//放一个假的数字，方便开发
			jobContext.putData("cpuLoadPerCore",Environment.getMaxCpuLoadPerCore());
			return 0;
		}
		RunShell shell = new RunShell("uptime");
		int exitCode = shell.run();
		if(exitCode==0){
			String result = shell.getResult();
			String[] tmps = result.split("\\s+");
			int len = tmps[9].length();
			String cpuloadstr = tmps[9].substring(0,len-1);
			Float cpuload = Float.valueOf(cpuloadstr);//最近1分钟系统的平均cpu负载
			Integer coreNum = workerContext.getCpuCoreNum();
			if (coreNum == null) {
				return -1;
			}
			Float cpuloadpercore = cpuload/workerContext.getCpuCoreNum();
			if((new Date().getTime()-date.getTime())>3*60*1000){
				ScheduleInfoLog.info("cpu load:"+ cpuloadstr +" core Number:"+coreNum+" rate:"+cpuloadpercore.toString());
				date=new Date();
			}
					// 可能迭代不到，迭代到的话这里就会return，所以最后面应该return -1
			jobContext.putData("cpuLoadPerCore", cpuloadpercore);
			return 0;
		}
		return -1;
	}
}
