package com.taobao.zeus.schedule.mvc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.taobao.zeus.broadcast.alarm.MailAlarm;
import com.taobao.zeus.broadcast.alarm.SMSAlarm;
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
	private ThreadLocal<ChainException> chainLocal=new ThreadLocal<ChainException>();
	public static class ChainException{
		final String causeJobId;
		Map<String, Integer> userCountMap=new HashMap<String, Integer>();
		GroupBean gb;
		public ChainException(String jobId,GroupBean gb){
			this.causeJobId=jobId;
			this.gb=gb;
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
				final String causeJobId=event.getJobException().getCauseJobId();
				if(chainLocal.get()==null || !chainLocal.get().getCauseJobId().equals(causeJobId)){
					GroupBean gb=readOnlyGroupManager.getGlobeGroupBean();
					chainLocal.set(new ChainException(causeJobId, gb));
				}
				final ChainException chain=chainLocal.get();
				final JobBean jobBean=chain.gb.getAllSubJobBeans().get(jobId);
				final ZeusUser owner=userManager.findByUid(jobBean.getJobDescriptor().getOwner());
				//延迟6秒发送邮件，保证日志已经输出到数据库
				new Thread(){
					public void run() {
						try {
							Thread.sleep(6000);
							StringBuffer sb=new StringBuffer();
							sb.append("Job任务(").append(jobId).append(")").append(jobBean.getJobDescriptor().getName()).append("运行失败");
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
								sb.append("失败原因:"+jobHistoryManager.findJobHistory(event.getHistory().getId()).getLog().getContent().replaceAll("\\n", "<br/>"));
								String msg= "Zeus报警 JobId:"+jobId+" 任务运行失败";
								if(!jobBean.getDepender().isEmpty()){
									msg+=",影响范围:"+getDependencyJobs(jobBean);
								}
								if(!causeJobId.equalsIgnoreCase(event.getJobId())){
									msg+="(根本原因:job "+causeJobId+"运行失败)";
								}
								mailAlarm.alarm(event.getHistory().getId(), msg, sb.toString());
							}
						} catch (Exception e) {
							log.error("邮件发送出现异常",e);
						}
					};
				}.start();
				
				String msg="Job任务("+jobId+"-"+owner.getName()+"):"+jobBean.getJobDescriptor().getName()+" 运行失败";
				if(!jobBean.getDepender().isEmpty()){
					msg+=",影响范围:"+getDependencyJobs(jobBean);
				}
				if(!causeJobId.equalsIgnoreCase(event.getJobId())){
					msg+="(根本原因:job "+causeJobId+"运行失败)";
				}
				
				//手机报警
				//只发送自动调度的报警  并且只在下班时间 或者周末发送
				if(event.getHistory().getTriggerType()==TriggerType.SCHEDULE){
					Calendar now=Calendar.getInstance();
					int hour=now.get(Calendar.HOUR_OF_DAY);
					int day=now.get(Calendar.DAY_OF_WEEK);
					if(day==Calendar.SATURDAY || day==Calendar.SUNDAY || hour<9 || hour>18){
						//smsAlarm.alarm(event.getHistory().getId(), "宙斯报警", "宙斯"+msg,chain);
						mailAlarm.alarm(event.getHistory().getId(), "宙斯报警", "宙斯"+msg,chain);
					}
				}
				
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