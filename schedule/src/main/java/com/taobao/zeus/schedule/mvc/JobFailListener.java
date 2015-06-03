package com.taobao.zeus.schedule.mvc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.taobao.zeus.broadcast.alarm.MailAlarm;
import com.taobao.zeus.broadcast.alarm.SMSAlarm;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.JobStatus.TriggerType;
import com.taobao.zeus.mvc.DispatcherListener;
import com.taobao.zeus.mvc.MvcEvent;
import com.taobao.zeus.schedule.mvc.event.JobFailedEvent;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.store.GroupBean;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.JobHistoryManager;
import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.ReadOnlyGroupManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.util.Tuple;
/**
 * 任务失败的监听
 * 当任务失败，需要发送邮件给相关人员
 * @author zhoufang
 *
 */
public class JobFailListener extends DispatcherListener{
	private static Logger log=LogManager.getLogger(JobFailListener.class);
	private GroupManager groupManager;
	private ReadOnlyGroupManager readOnlyGroupManager;
	private UserManager userManager;
	private JobHistoryManager jobHistoryManager;
	private MailAlarm mailAlarm;
	private SMSAlarm smsAlarm;
	public JobFailListener(MasterContext context){
		groupManager=(GroupManager) context.getGroupManager();
		readOnlyGroupManager=(ReadOnlyGroupManager)context.getApplicationContext().getBean("readOnlyGroupManager");
		userManager=(UserManager)context.getApplicationContext().getBean("userManager");
		jobHistoryManager=(JobHistoryManager)context.getJobHistoryManager();
		mailAlarm=(MailAlarm) context.getApplicationContext().getBean("mailAlarm");
		smsAlarm=(SMSAlarm) context.getApplicationContext().getBean("smsAlarm");
	}
	//private ThreadLocal<ChainException> chainLocal=new ThreadLocal<ChainException>();
	public static class ChainException{
		final String causeJobId;
		Map<String, Integer> userCountMap=new HashMap<String, Integer>();
//		GroupBean gb;
		public ChainException(String jobId,GroupBean gb){
			this.causeJobId=jobId;
//			this.gb=gb;
		}
		public Map<String, Integer> getUserCountMap() {
			return userCountMap;
		}
		public String getCauseJobId() {
			return causeJobId;
		}
	}
	@Override
	public void beforeDispatch(MvcEvent mvce) {
		try {
			if(mvce.getAppEvent() instanceof JobFailedEvent){
				final JobFailedEvent event=(JobFailedEvent) mvce.getAppEvent();
				final String jobId=event.getJobId();
//				final String causeJobId=event.getJobException().getCauseJobId();
//				if(chainLocal.get()==null || !chainLocal.get().getCauseJobId().equals(causeJobId)){
//					GroupBean gb=readOnlyGroupManager.getGlobeGroupBean();
//					chainLocal.set(new ChainException(causeJobId, gb));
//				}
//				final ChainException chain=chainLocal.get();
//				final JobBean jobBean=chain.gb.getAllSubJobBeans().get(jobId);
				final JobDescriptor jobDescriptor = groupManager.getJobDescriptor(jobId).getX();
				final ZeusUser owner=userManager.findByUid(jobDescriptor.getOwner());
				//延迟6秒发送邮件，保证日志已经输出到数据库
				new Thread(){
					public void run() {
						try {
							Thread.sleep(6000);
							StringBuffer sb=new StringBuffer();
							sb.append("Job任务(").append(jobId).append(")").append(jobDescriptor.getName()).append("运行失败");
							sb.append("<br/>");
							Map<String, String> properties=jobDescriptor.getProperties();
							if(properties!=null){
								String plevel=properties.get("run.priority.level");
								if("1".equals(plevel)){
									sb.append("Job任务优先级: ").append("low").append("，");
								}else if("2".equals(plevel)){
									sb.append("Job任务优先级: ").append("medium").append("，");
								}else if("3".equals(plevel)){
									sb.append("Job任务优先级: ").append("high").append("，");
								}
							}
							String owner=jobDescriptor.getOwner();
							sb.append("Job任务owner: ").append(owner);
							sb.append("<br/>");
							String type="";
							if(event.getTriggerType()==TriggerType.MANUAL){
								type="手动触发";
							}else if(event.getTriggerType()==TriggerType.MANUAL_RECOVER){
								type="手动恢复";
							}else if(event.getTriggerType()==TriggerType.SCHEDULE){
								type="自动调度";
							}
							sb.append("Job任务的触发类型为:"+type).append("<br/>");
							if(event.getHistory()!=null){
								sb.append("失败原因:<br/>"+jobHistoryManager.findJobHistory(event.getHistory().getId()).getLog().getContent().replaceAll("\\n", "<br/>"));
								String msg= "Zeus报警 JobId:"+jobId+" ("+jobDescriptor.getName()+") 任务运行失败";
								int runCount = event.getRunCount();
							    int rollBackTime = event.getRollBackTime();
							    if (runCount > rollBackTime) {
							    	if(event.getTriggerType()==TriggerType.SCHEDULE){
							    		msg = "<font style=\"color:red\">【严重】</font>" + msg;
							    	}else{
							    		msg = "【警告】" + msg;
							    	}
							    }else{
							    	msg = "【提醒】" + msg;
							    }
//								if(!causeJobId.equalsIgnoreCase(event.getJobId())){
//									msg+="(根本原因:job "+causeJobId+"运行失败)";
//								}
								mailAlarm.alarm(event.getHistory().getId(), msg, sb.toString());
								//smsAlarm.alarm(event.getHistory().getId(), msg, sb.toString());
							}
						} catch (Exception e) {
							log.error("邮件发送出现异常",e);
						}
					};
				}.start();
				new Thread(){
					@Override
					public void run(){
						String msg="Job任务("+jobId+"-"+owner.getName()+"):" + jobDescriptor.getName()+" 运行失败";
						//优先级低的不NOC告警
						String priorityLevel = jobDescriptor.getProperties().get("run.priority.level");
						if(priorityLevel == null || !priorityLevel.trim().equals("1")){
							//手机报警
							//最后一次重试的时候发送
							//只发送自动调度的报警  并且只在下班时间 或者周末发送
							if(event.getHistory().getTriggerType()==TriggerType.SCHEDULE){
								int runCount = event.getRunCount();
							    int rollBackTime = event.getRollBackTime();
								Calendar now=Calendar.getInstance();
								int hour=now.get(Calendar.HOUR_OF_DAY);
								int day=now.get(Calendar.DAY_OF_WEEK);
								if (runCount > rollBackTime) {
									if(day==Calendar.SATURDAY || day==Calendar.SUNDAY || hour<9 || hour>18){
										try {
											smsAlarm.alarm(event.getHistory().getId(), "宙斯报警", "宙斯"+msg, null);
										} catch (Exception e) {
											log.error("NOC发送出现异常",e);
										}
									}
								}
							}
						}
					}
				}.start();
			}
		} catch (Exception e) {
			//处理异常，防止后续的依赖任务受此影响，无法正常执行
			log.error("失败任务，发送通知出现异常",e);
		}
	}
	
	private int getDependencyJobs(JobBean job){
		int result=job.getDepender().size();
		if(!job.getDepender().isEmpty()){
			for(JobBean jb:job.getDepender()){
				result+=getDependencyJobs(jb);
			}
		}
		return result;
	}
}