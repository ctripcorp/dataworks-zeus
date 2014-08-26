package com.taobao.zeus.jobs.sub.tool;

import java.util.Date;

import net.sf.json.JSONObject;

import org.springframework.context.ApplicationContext;

import com.taobao.zeus.broadcast.notify.ZKResultNotify;
import com.taobao.zeus.jobs.AbstractJob;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.model.processer.ZooKeeperProcesser;

public class ZooKeeperJob extends AbstractJob{
	private ZKResultNotify zkResultNotify;
	private ZooKeeperProcesser processer;
	public ZooKeeperJob(JobContext jobContext,ZooKeeperProcesser processer,ApplicationContext applicationContext) {
		super(jobContext);
		this.processer=processer;
		this.zkResultNotify=(ZKResultNotify)applicationContext.getBean("zkResultNotify");
	}
	
	@Override
	public Integer run() throws Exception {
		Integer exitCode=0;
		try {
			Boolean data=jobContext.getCoreExitCode()==0?true:false;
			
			if(processer==null){
				JSONObject content=new JSONObject();
				content.put("id",getJobContext().getJobHistory().getJobId());
				content.put("historyId", getJobContext().getJobHistory().getId());
				content.put("status", jobContext.getCoreExitCode()==0?true:false);
				content.put("time", new Date().getTime());
				zkResultNotify.send(jobContext.getJobHistory().getId(), content.toString());
			}else{
				String host=processer.getHost();
				String path=processer.getPath();
				log("开始通知ZooKeeper host:"+host+",path:"+path);
				zkResultNotify.send(host, path, data.toString());
				log("ZooKeeper通知完成");
			}
			
		} catch (Exception e) {
			exitCode=-1;
			log("ZK通知发送失败");
			log(e);
		}
		
		return exitCode;
	}

	@Override
	public void cancel() {
		canceled=true;
	}

}
