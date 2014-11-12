package com.taobao.zeus.schedule.mvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.tool.CancelHadoopJob;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobDescriptor.JobScheduleType;
import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.JobStatus.Status;
import com.taobao.zeus.model.JobStatus.TriggerType;
import com.taobao.zeus.mvc.AppEvent;
import com.taobao.zeus.mvc.Controller;
import com.taobao.zeus.mvc.Dispatcher;
import com.taobao.zeus.schedule.hsf.CacheJobDescriptor;
import com.taobao.zeus.schedule.mvc.event.Events;
import com.taobao.zeus.schedule.mvc.event.JobFailedEvent;
import com.taobao.zeus.schedule.mvc.event.JobLostEvent;
import com.taobao.zeus.schedule.mvc.event.JobMaintenanceEvent;
import com.taobao.zeus.schedule.mvc.event.JobSuccessEvent;
import com.taobao.zeus.schedule.mvc.event.ScheduleTriggerEvent;
import com.taobao.zeus.socket.master.Master;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.JobHistoryManager;
import com.taobao.zeus.util.DateUtil;
import com.taobao.zeus.util.PropertyKeys;

public class JobController extends Controller {

	private final String jobId;
	private CacheJobDescriptor cache;
	private JobHistoryManager jobHistoryManager;
	private GroupManager groupManager;

	private Master master;
	private MasterContext context;
	
	private static Logger log = LogManager.getLogger(JobController.class);

	public JobController(MasterContext context, Master master, String jobId) {
		this.jobId = jobId;
		this.jobHistoryManager = context.getJobHistoryManager();
		groupManager = context.getGroupManager();
		this.cache = new CacheJobDescriptor(jobId, groupManager);
		this.master = master;
		this.context = context;
		registerEventTypes(Events.Initialize);
	}
	
	private final Date getForver(){
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2099-12-31 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public boolean canHandle(AppEvent event, boolean bubbleDown) {
		if (super.canHandle(event, bubbleDown)) {
			JobDescriptor jd = cache.getJobDescriptor();
			if (jd == null) {
				autofix();
				return false;
			}
			return jd.getAuto();
		}
		return false;
	}

	@Override
	public void handleEvent(AppEvent event) {
		try {
			if (event instanceof JobSuccessEvent) {
				successEventHandle((JobSuccessEvent) event);
			} else if (event instanceof JobFailedEvent) {
				failedEventHandle((JobFailedEvent) event);
			} else if (event instanceof ScheduleTriggerEvent) {
				triggerEventHandle((ScheduleTriggerEvent) event);
			} else if (event instanceof JobMaintenanceEvent) {
				maintenanceEventHandle((JobMaintenanceEvent) event);
			} else if (event instanceof JobLostEvent) {
				lostEventHandle((JobLostEvent) event);
			} else if (event.getType() == Events.Initialize) {
				initializeEventHandle();
			}
		} catch (Exception e) {
			// catch所有的异常，保证本job的异常不影响其他job的运行
			ScheduleInfoLog.error("JobId:" + jobId + " handleEvent error", e);
		}
	}

	private void initializeEventHandle() {
		JobStatus jobStatus = groupManager.getJobStatus(jobId);
//		System.out.println("jobId: "+jobId+" jobStatus:"+jobStatus.getStatus());
		if (jobStatus != null) {
			// 启动时发现在RUNNING 状态，说明上一次运行的结果丢失，将立即进行重试
			if (jobStatus.getStatus() == Status.RUNNING) {
				log.error("jobId=" + jobId
						+ " 处于RUNNING状态，说明该JOB状态丢失，立即进行重试操作...");
				// 搜索上一次运行的日志，从日志中提取jobid 进行kill
				String operator = null;
				if (jobStatus.getHistoryId() != null) {
					JobHistory history = jobHistoryManager
							.findJobHistory(jobStatus.getHistoryId());
					// 特殊情况下，有可能history查询为空
					if (history != null
							&& history.getStatus() == Status.RUNNING) {
						operator = history.getOperator();
						try {
							JobContext temp = JobContext.getTempJobContext(JobContext.MANUAL_RUN);
							history.setIllustrate("启动服务器发现正在running状态，判断状态已经丢失，进行重试操作");
							temp.setJobHistory(history);
							new CancelHadoopJob(temp).run();
							master.run(history);
						} catch (Exception e) {
							// 忽略
						}
					}
				}else{
					JobHistory history = new JobHistory();
					history.setIllustrate("启动服务器发现正在running状态，判断状态已经丢失，进行重试操作");
					history.setOperator(operator);
					history.setTriggerType(TriggerType.MANUAL_RECOVER);
					history.setJobId(jobId);
					JobDescriptor jobDescriptor = groupManager.getUpstreamJobBean(jobId).getJobDescriptor();
					history.setToJobId(jobDescriptor.getToJobId());
					if(jobDescriptor != null){
						history.setOperator(jobDescriptor.getOwner() == null ? null : jobDescriptor.getOwner());
					}
					context.getJobHistoryManager().addJobHistory(history);
					master.run(history);
				}
			}
		}

		JobDescriptor jd = cache.getJobDescriptor();
		// 如果是定时任务，启动定时程序
		if (jd.getAuto() && jd.getScheduleType() == JobScheduleType.Independent) {
			String cronExpression = jd.getCronExpression();
			try {
				CronTrigger trigger = new CronTrigger(jd.getId(), "zeus",
						cronExpression);
				/**************2014-09-14**************
				Date date=null;  
			    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			    date=formatter.parse(cronExpression);  
				SimpleTrigger trigger = new SimpleTrigger(jd.getId(), "zeus", date, null, 0, 0L);
				//**********************************************************************************/
				JobDetail detail = new JobDetail(jd.getId(), "zeus",
						TimerJob.class);
				detail.getJobDataMap().put("jobId", jd.getId());
				detail.getJobDataMap().put("dispatcher",
						context.getDispatcher());
				context.getScheduler().scheduleJob(detail, trigger);
			} catch (Exception e) {
				if (e instanceof SchedulerException
						&& "Based on configured schedule, the given trigger will never fire."
								.equals(e.getMessage())) {
					// 定时器已经不会被触发了，关闭该job的自动调度功能
					jd.setAuto(false);
					try {
						groupManager.updateJob(jd.getOwner(), jd);
					} catch (ZeusException e1) {
						log.error("JobId:" + jobId + " 更新失败", e1);
					}
					cache.refresh();
				} else {
					log.error("JobId:" + jobId + " 定时程序启动失败", e);
				}
			}
		}
		// 周期任务，并且没有依赖的情况下，直接根据开始时间执行
		if (jd.getAuto()
				&& jd.getScheduleType() == JobScheduleType.CyleJob
				&& (jd.getDependencies() == null || jd.getDependencies()
						.isEmpty())) {
			initCycleJob(jd);
		}
	}

	private void initCycleJob(JobDescriptor jd) {
		Date date = null;
		try {
			date = DateUtil.timestamp2Date(jd.getStartTimestamp(),
					DateUtil.getDefaultTZStr());
		} catch (ParseException e) {
			date = new Date();
			log.error("parse job start timestamp to date failed,", e);
		}
		SimpleTrigger simpleTrigger=null;
//		if(jd.getCycle().equals("hour")){
//			simpleTrigger = new SimpleTrigger(jd.getId(), "zeus",
//					date, this.getForver(), SimpleTrigger.REPEAT_INDEFINITELY, 60*60*1000);
//		}
//		else if(jd.getCycle().equals("day")){
//			simpleTrigger = new SimpleTrigger(jd.getId(), "zeus",
//					date, this.getForver(), SimpleTrigger.REPEAT_INDEFINITELY, 24*60*60*1000);
//		}
//		else{
			simpleTrigger = new SimpleTrigger(jd.getId(), "zeus",
					date, null, 0, 0L);
//		}
		
		JobDetail detail = null;
		// 先查看之前是否存在该任务的调度，如果存在，先删除
		try {
			detail = context.getScheduler().getJobDetail(jd.getId(), "zeus");
		} catch (SchedulerException e) {
			log.error(e);
		}
		if (detail != null) {
			try {
				context.getScheduler().deleteJob(jobId, "zeus");
				log.error("schedule remove job with jobId:" + jobId);
			} catch (SchedulerException e) {
				log.error(e);
			}
			detail = null;
		}

		detail = new JobDetail(jd.getId(), "zeus", TimerJob.class);
		detail.getJobDataMap().put("jobId", jd.getId());
		detail.getJobDataMap().put("dispatcher", context.getDispatcher());
		try {
			context.getScheduler().scheduleJob(detail, simpleTrigger);
			ScheduleInfoLog.info("Add job JobId:" + jobId
					+ " to schedule");
		} catch (SchedulerException e) {
			log.error("schedule simple job failed,", e);
		}
	}

	@Override
	protected void destory() {
		try {
			JobDetail detail = context.getScheduler().getJobDetail(jobId,
					"group");
			if (detail != null) {
				context.getScheduler().deleteJob(jobId, "zeus");
			}
		} catch (SchedulerException e) {
			log.error(e);
		}
	}

	@Override
	public boolean canHandle(AppEvent event) {
		if (super.canHandle(event)) {
			return true;
		}
		if (event instanceof JobSuccessEvent || event instanceof JobFailedEvent
				|| event instanceof ScheduleTriggerEvent
				|| event instanceof JobMaintenanceEvent
				|| event instanceof JobLostEvent) {
			return true;
		}
		return false;
	}

	/**
	 * 维护 当Job被更新后，调度系统需要相应的进行修改
	 * 
	 * @param event
	 */
	private void maintenanceEventHandle(JobMaintenanceEvent event) {
		if (event.getType() == Events.UpdateJob
				&& jobId.equals(event.getJobId())) {
			autofix();
		}
	}
	
	/**
	 * 漏跑JOB，重新依赖调度
	 * 
	 * @param event
	 */
	private void lostEventHandle(JobLostEvent event) {
		if (event.getType() == Events.UpdateJob
				&& jobId.equals(event.getJobId())) {
			cache.refresh();
			JobDescriptor jd = cache.getJobDescriptor();
			if(jd!=null && jd.getAuto()){
				JobStatus jobStatus = groupManager.getJobStatus(jobId);
				if(jobStatus.getStatus() == null || jobStatus.getStatus() == Status.WAIT){
					Date now = new Date();
					SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
					String currentDateStr = df.format(now)+"0000";
					if(Long.parseLong(jobId) < Long.parseLong(currentDateStr)){
						JobHistory history = new JobHistory();
						history.setIllustrate("漏跑任务,自动恢复执行");
						history.setTriggerType(TriggerType.MANUAL_RECOVER);
						history.setJobId(jobId);
						history.setToJobId(jd.getToJobId());
						if(jd != null){
							history.setOperator(jd.getOwner() == null ? null : jd.getOwner());
						}
						context.getJobHistoryManager().addJobHistory(history);
						master.run(history);
					}
				}
			}
		}
	}

	/**
	 * 收到执行任务成功的事件的处理流程
	 * 
	 * @param event
	 */
	private void successEventHandle(JobSuccessEvent event) {
		if (event.getTriggerType() == TriggerType.MANUAL) {
			return;
		}
		String eId = event.getJobId();
		JobDescriptor jobDescriptor = cache.getJobDescriptor();
		if (jobDescriptor == null) {
			autofix();
			return;
		}
		if (!jobDescriptor.getAuto()) {
			return;
		}

		if (jobDescriptor.getScheduleType() == JobScheduleType.Independent) {
			return;
		}

		if (jobDescriptor.getScheduleType() == JobScheduleType.CyleJob) {
			cycleJobSuccessHandle(event);
			return;
		}

		if (!jobDescriptor.getDependencies().contains(eId)) {
			return;
		}

		JobStatus jobStatus = null;
		synchronized (this) {
			jobStatus = groupManager.getJobStatus(jobId);
			JobBean bean = groupManager.getUpstreamJobBean(jobId);
			String cycle = bean.getHierarchyProperties().getProperty(
					PropertyKeys.DEPENDENCY_CYCLE);
			if (cycle != null && !"".equals(cycle)) {
				Map<String, String> dep = jobStatus.getReadyDependency();
				//判断依赖周期是同一天，如果依赖Job的完成时间与当前时间不是同一天，就移除此依赖关系
				if ("sameday".equals(cycle)) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					String now = format.format(new Date());
					for (String key : new HashSet<String>(dep.keySet())) {
						String d = format.format(new Date(Long.valueOf(dep
								.get(key))));
						if (!now.equals(d)) {
							jobStatus.getReadyDependency().remove(key);
							ScheduleInfoLog.info("JobId:" + jobId
									+ " remove overdue dependency " + key);
						}
					}
				}
			}

			ScheduleInfoLog.info("JobId:" + jobId
					+ " received a successed dependency job with jobId:"
					+ event.getJobId());

			ScheduleInfoLog.info("JobId:" + jobId + " the dependency jobId:"
					+ event.getJobId() + " record it");
			jobStatus.getReadyDependency().put(eId,
					String.valueOf(new Date().getTime()));

			groupManager.updateJobStatus(jobStatus);
		}
		boolean allComplete = true;
		for (String key : jobDescriptor.getDependencies()) {
			if (jobStatus.getReadyDependency().get(key) == null) {
				allComplete = false;
				break;
			}
		}
		if (allComplete) {
			ScheduleInfoLog.info("JobId:" + jobId
					+ " all dependency jobs is ready,run!");
			startNewJob(event.getTriggerType(), jobDescriptor, jobId);
		} else {
			ScheduleInfoLog.info("JobId:" + jobId
					+ " some of dependency is not ready,waiting!");
		}
	}

	private void startNewJob(TriggerType type, JobDescriptor jobDescriptor,
			String jobID) {
		JobHistory history = new JobHistory();
		history.setIllustrate("依赖任务全部到位，开始执行");
		history.setTriggerType(TriggerType.SCHEDULE);
		history.setJobId(jobId);
		System.out.println("依赖任务执行的operator ："+jobDescriptor.getOwner());
		history.setOperator(jobDescriptor.getOwner() == null ? null : jobDescriptor.getOwner());
		history.setToJobId(jobDescriptor.getToJobId() == null ? null : jobDescriptor.getToJobId());
		history.setExecuteHost(jobDescriptor.getHost());
		context.getJobHistoryManager().addJobHistory(history);
		history = master.run(history);
		if (history.getStatus() == Status.FAILED) {
			ZeusJobException exception = new ZeusJobException(
					history.getJobId(), history.getLog().getContent());
			JobFailedEvent jfe = new JobFailedEvent(jobDescriptor.getId(),
					type, history, exception);
			ScheduleInfoLog.info("JobId:" + jobId
					+ " is fail,dispatch the fail event");
			// 广播消息
			context.getDispatcher().forwardEvent(jfe);
		}
	}

	/*
	 * 处理周期任务成功事件 上面一句判断了该任务依赖一句完成的任务
	 */
	private void cycleJobSuccessHandle(JobSuccessEvent event) {
		String eId = event.getJobId();
		JobDescriptor jobDescriptor = cache.getJobDescriptor();
		JobDescriptor jd = jobDescriptor.getCopy();
		JobDescriptor eIobDescriptor = groupManager.getJobDescriptor(eId)
				.getX();

		String nextStartTime = null;
		String nextSSTime = null;
		String nextSETime = null;
		long nextTS = 0;
		// 独立的周期任务，运算下次开始时间
		if (eId.equals(jobId)
				&& (jobDescriptor.getDependencies() == null || jobDescriptor
						.getDependencies().isEmpty())) {
			try {
				if (jobDescriptor.getCycle().equals("hour")) {
					nextStartTime = DateUtil.getDelayTime(1,
							jobDescriptor.getStartTime());
					nextSSTime = DateUtil.getDelayTime(1,
							jobDescriptor.getStatisStartTime());
					nextSETime = DateUtil.getDelayTime(1,
							jobDescriptor.getStatisEndTime());
					nextTS = jobDescriptor.getStartTimestamp() + 60 * 60 * 1000;
				}
				if (jobDescriptor.getCycle().equals("day")) {
					nextStartTime = DateUtil.getDelayTime(24,
							jobDescriptor.getStartTime());
					nextSSTime = DateUtil.getDelayTime(24,
							jobDescriptor.getStatisStartTime());
					nextSETime = DateUtil.getDelayTime(24,
							jobDescriptor.getStatisEndTime());
					nextTS = jobDescriptor.getStartTimestamp() + 24 * 60 * 60
							* 1000;
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jd.setStartTime(nextStartTime);
			jd.setStatisEndTime(nextSETime);
			jd.setStatisStartTime(nextSSTime);
			jd.setStartTimestamp(nextTS);
			JobStatus js = new JobStatus();
			js.setJobId(eId);
			js.setStatus(JobStatus.Status.WAIT);
			try {
				groupManager.updateJob(jd.getOwner(), jd);
				groupManager.updateJobStatus(js);
			} catch (ZeusException e) {
				log.error("", e);
				e.printStackTrace();
			}
			//initCycleJob(jd);
			cache.refresh();
			return;
		}

		// 如果当前任务依赖于已经完成的任务，则在当前任务中保存已经完成的依赖，
		// 并检查当前任务是否可以执行

		if (!jobDescriptor.getDependencies().contains(eId)) {
			return;
		}

		JobStatus jobStatus = null;

		synchronized (this) {
			jobStatus = groupManager.getJobStatus(jobId);
			ScheduleInfoLog.info("JobId:" + jobId
					+ " received a successed dependency job with jobId:" + eId
					+ " statisTime:" + event.getStatisEndTime());
			jobStatus.getReadyDependency().put(eId, event.getStatisEndTime());
			groupManager.updateJobStatus(jobStatus);
		}
		boolean allComplete = true;
		for (String key : jobDescriptor.getDependencies()) {
			if (jobStatus.getReadyDependency().get(key) == null
					|| !jobStatus.getReadyDependency().get(key)
							.equals(jobDescriptor.getStatisEndTime())) {
				allComplete = false;
				break;
			}
		}
		// 任务依赖的周期都必须相同并且任务的结束时间也必须相同
		String cycle = jobDescriptor.getDepdCycleJob().get(eId);
		for (Entry<String, String> entry : jobDescriptor.getDepdCycleJob()
				.entrySet()) {
			if (!entry.getValue().equals(cycle)) {
				ScheduleInfoLog.error("JobId:" + jobId
						+ " has different cycle dependence", null);
				allComplete = false;
				break;
			}
		}
		if (allComplete) {
			// 如果周期一样
			if (eIobDescriptor.getCycle().equals(jobDescriptor.getCycle())) {
				jd.setStatisEndTime(jobDescriptor.getStatisEndTime());
				jd.setStartTime(jobDescriptor.getStartTime());
				jd.setStatisStartTime(jobDescriptor.getStatisStartTime());
				jd.setId(jobId);
				jd.setCycle(jobDescriptor.getCycle());

				try {
					if (jobDescriptor.getCycle().equals("hour")) {
						jobDescriptor.setStatisStartTime(DateUtil.getDelayTime(
								1, jobDescriptor.getStatisStartTime()));
						jobDescriptor.setStatisEndTime(DateUtil.getDelayTime(
								1, jobDescriptor.getStatisEndTime()));
						jobDescriptor.setStartTime(DateUtil.getDelayTime(
								1, jobDescriptor.getStartTime()));
					}
					if(jobDescriptor.getCycle().equals("day")){
						jobDescriptor.setStatisStartTime(DateUtil.getDelayTime(
								24, jobDescriptor.getStatisStartTime()));
						jobDescriptor.setStatisEndTime(DateUtil.getDelayTime(
								24, jobDescriptor.getStatisEndTime()));
						jobDescriptor.setStartTime(DateUtil.getDelayTime(
								24, jobDescriptor.getStartTime()));
					}
					groupManager.updateJob(jobDescriptor.getOwner(), jobDescriptor);
					cache.refresh();
				} catch (ParseException e) {
					ScheduleInfoLog.error("parse date failed", e);
				} catch (ZeusException e) {
					ScheduleInfoLog.error("update job failed", e);
				}
				ScheduleInfoLog.info("JobId:"+jobId+" all dependence for "+jd.getStatisEndTime()+" is ready,start");
				runJob(jd);

			} else {
				// 如果周期不一样，因为只有天任务依赖小时任务，没有小时任务依赖天，所以可以判断自己肯定是天任务
				// 而完成的是小时任务,因此需要判断该小时是否是23时即可

				if (event.getStatisEndTime().equals(
						jobDescriptor.getStatisEndTime())) {
					jd.setStatisEndTime(jobDescriptor.getStatisEndTime());
					jd.setStartTime(jobDescriptor.getStartTime());
					jd.setStatisStartTime(jobDescriptor.getStatisStartTime());
					jd.setId(jobId);
					jd.setCycle(jobDescriptor.getCycle());
					try {
						jobDescriptor.setStatisStartTime(DateUtil.getDelayTime(
								24, jobDescriptor.getStatisStartTime()));
						jobDescriptor.setStatisEndTime(DateUtil.getDelayTime(
								24, jobDescriptor.getStatisEndTime()));
						jobDescriptor.setStartTime(DateUtil.getDelayTime(
								24, jobDescriptor.getStartTime()));
						groupManager.updateJob(jobDescriptor.getOwner(), jobDescriptor);
						cache.refresh();
					} catch (ParseException e) {
						ScheduleInfoLog.error("parse date failed", e);
					} catch (ZeusException e) {
						ScheduleInfoLog.error("update job failed", e);
					}
					ScheduleInfoLog.info("JobId:"+jobId+" all dependence for "+jd.getStatisEndTime()+" is ready,start");
					runJob(jd);
				}
			}
		} else {
			ScheduleInfoLog.info("JobId:" + jobId + " is not ready,waiting!");
		}

	}

	/**
	 * 收到执行任务失败的事件的处理流程
	 * 
	 * ?疑惑 当依赖的一个Job失败时，本Job也自动失败了。但是本Job依赖的其他Job的状态是否还保存？ 1. 2.抛出失败的消息
	 * 
	 * @param event
	 */
	private void failedEventHandle(JobFailedEvent event) {
		JobDescriptor jobDescriptor = cache.getJobDescriptor();
		if (jobDescriptor == null) {
			autofix();
			return;
		}
		if (!jobDescriptor.getAuto()) {
			return;
		}
		if (jobDescriptor.getDependencies().contains(event.getJobId())) {// 本Job依赖失败的Job
			if (event.getTriggerType() == TriggerType.SCHEDULE) {// 依赖的Job
																	// 的失败类型是
																	// SCHEDULE类型
				// 自身依赖的Job失败了，表明自身也无法继续执行，抛出失败的消息
				ZeusJobException exception = new ZeusJobException(event
						.getJobException().getCauseJobId(), "jobId:"
						+ jobDescriptor.getId() + " 失败，原因是依赖的Job："
						+ event.getJobId() + " 执行失败", event.getJobException());
				ScheduleInfoLog.info("jobId:" + jobId
						+ " is fail,as dependendy jobId:"
						+ jobDescriptor.getId() + " is failed");
				// 记录进History日志
				JobHistory history = new JobHistory();
				history.setStartTime(new Date());
				history.setEndTime(new Date());
				history.setExecuteHost(null);
				history.setJobId(jobId);
				history.setToJobId(jobDescriptor.getToJobId() == null ? null : jobDescriptor.getToJobId());
				history.setTriggerType(event.getTriggerType());
				history.setStatus(Status.FAILED);
				history.getLog().appendZeusException(exception);
				history.setStatisEndTime(jobDescriptor.getStatisEndTime());
				history.setTimezone(jobDescriptor.getTimezone());
				history.setCycle(jobDescriptor.getCycle());
				history.setOperator(jobDescriptor.getOwner() == null ? null : jobDescriptor.getOwner());
				history = jobHistoryManager.addJobHistory(history);
				jobHistoryManager.updateJobHistoryLog(history.getId(), history
						.getLog().getContent());

				JobFailedEvent jfe = new JobFailedEvent(jobDescriptor.getId(),
						event.getTriggerType(), history, exception);

				ScheduleInfoLog.info("JobId:" + jobId
						+ " is fail,dispatch the fail event");
				// 广播消息
				context.getDispatcher().forwardEvent(jfe);
			}
		}
	}

	/**
	 * 自动修复 因为可能会碰到很多异常情况，比如本该删除的job没有删除，本该更新的job没有更新等等
	 * 这里做统一的处理，处理完成之后，保证与数据库的设置是一致的
	 */
	private void autofix() {
		cache.refresh();
		JobDescriptor jd = cache.getJobDescriptor();
		if (jd == null) {// 如果这是一个删除操作，这里将会是null 忽略
			// job被删除，需要清理
			context.getDispatcher().removeController(this);
			destory();
			ScheduleInfoLog.info("schedule remove job with jobId:" + jobId);
			return;
		}
		JobDetail detail = null;
		try {
			detail = context.getScheduler().getJobDetail(jobId, "zeus");
		} catch (SchedulerException e) {
			log.error(e);
		}
		// 判断自动调度的开关
		if (!jd.getAuto()) {
			if (detail != null) {
				try {
					context.getScheduler().deleteJob(jobId, "zeus");
					log.error("schedule remove job with jobId:" + jobId);
				} catch (SchedulerException e) {
					log.error(e);
				}
			}
			return;
		}
		if (jd.getScheduleType() == JobScheduleType.Dependent) {// 如果是依赖任务
			if (detail != null) {// 说明原来是独立任务，现在变成依赖任务，需要删除原来的定时调度
				try {
					context.getScheduler().deleteJob(jobId, "zeus");
					ScheduleInfoLog
							.info("JobId:"
									+ jobId
									+ " from independent to dependent ,remove from schedule");
				} catch (SchedulerException e) {
					log.error(e);
				}
			}

		} else if (jd.getScheduleType() == JobScheduleType.Independent) {// 如果是独立任务
			ScheduleInfoLog.info("JobId:" + jobId + " independent job,update");
			try {
				if (detail != null) {
					context.getScheduler().deleteJob(jobId, "zeus");
					ScheduleInfoLog.info("JobId:" + jobId
							+ " remove from schedule");
				}
				CronTrigger trigger = new CronTrigger(jd.getId(), "zeus",
						jd.getCronExpression());
				detail = new JobDetail(jd.getId(), "zeus", TimerJob.class);
				detail.getJobDataMap().put("jobId", jd.getId());
				detail.getJobDataMap().put("dispatcher",
						context.getDispatcher());
				context.getScheduler().scheduleJob(detail, trigger);
				ScheduleInfoLog.info("JobId:" + jobId
						+ " add job to schedule for refresh");
			} catch (SchedulerException e) {
				log.error(e);
			} catch (ParseException e) {
				log.error(e);
			}
		} else if (jd.getScheduleType() == JobScheduleType.CyleJob
				&& (jd.getDependencies() == null || jd.getDependencies()
						.isEmpty())) {
			initCycleJob(jd);
		}
	}

	/**
	 * 收到定时触发任务的事件的处理流程
	 * 
	 * @param event
	 */
	private void triggerEventHandle(ScheduleTriggerEvent event) {
		String eId = event.getJobId();
		JobDescriptor jobDescriptor = cache.getJobDescriptor();
		if (jobDescriptor == null) {// 说明job被删除了，这是一个异常状况，autofix
			autofix();
			return;
		}
		if (!eId.equals(jobDescriptor.getId())) {
			return;
		}
		ScheduleInfoLog.info("JobId:" + jobId
				+ " receive a timer trigger event,statisTime is:"
				+ jobDescriptor.getStatisEndTime());
		runJob(jobDescriptor);
	}

	private void runJob(JobDescriptor jobDescriptor) {
		JobHistory history = new JobHistory();
		history.setJobId(jobDescriptor.getId());
		history.setToJobId(jobDescriptor.getToJobId() == null ? null : jobDescriptor.getToJobId());
		history.setTriggerType(TriggerType.SCHEDULE);
		history.setStatisEndTime(jobDescriptor.getStatisEndTime());
		history.setTimezone(jobDescriptor.getTimezone());
		history.setCycle(jobDescriptor.getCycle());
		history.setExecuteHost(jobDescriptor.getHost());
		history.setOperator(jobDescriptor.getOwner() == null ? null : jobDescriptor.getOwner());
		context.getJobHistoryManager().addJobHistory(history);
		master.run(history);
	}

	/*
	 * private void run(final JobDescriptor jobDescriptor,final TriggerType
	 * type,String illustrate){ //更新状态 JobStatus
	 * status=groupManager.getJobStatus(jobId);
	 * status.setStatus(Status.RUNNING); final JobHistory history=new
	 * JobHistory(); history.setJobId(jobId); history.setTriggerType(type);
	 * history.setIllustrate(illustrate); history.setStatus(Status.RUNNING);
	 * jobHistoryManager.addJobHistory(history);
	 * groupManager.updateJobStatus(status);
	 * 
	 * Thread thread=new Thread(new Runnable() {
	 * 
	 * @Override public void run() {
	 * ScheduleInfoLog.info("JobId:"+jobId+" run start"); boolean success=false;
	 * Exception exception=null; try { int
	 * exitCode=workerService.executeJob(history.getId()); if(exitCode==0 ||
	 * exitCode==ExitCodes.NOTIFY_ZK_FAIL){ success=true; }else{ success=false;
	 * } } catch (Exception e) { success=false; exception=e;
	 * log.error(String.format("JobId:%s run failed ", jobDescriptor.getId()),
	 * e); } JobStatus jobstatus=groupManager.getJobStatus(jobId);
	 * jobstatus.setStatus(Status.WAIT); if(success &&
	 * (type==TriggerType.SCHEDULE || type==TriggerType.MANUAL_RECOVER )){
	 * ScheduleInfoLog.info("JobId:"+jobId+" clear ready dependency");
	 * jobstatus.setReadyDependency(new HashMap<String, String>()); }
	 * groupManager.updateJobStatus(jobstatus);
	 * 
	 * 
	 * if(!success){ //运行失败，更新失败状态，发出失败消息 if(exception!=null){ exception=new
	 * ZeusException(String.format("JobId:%s run failed ",
	 * jobDescriptor.getId()), exception); }else{ exception=new
	 * ZeusException(String.format("JobId:%s run failed ",
	 * jobDescriptor.getId())); }
	 * ScheduleInfoLog.info("JobId:"+jobId+" run fail and dispatch the fail event"
	 * ); JobFailedEvent jfe=new
	 * JobFailedEvent(jobDescriptor.getId(),type,jobHistoryManager
	 * .findJobHistory(history.getId()),exception);
	 * dispatcher.forwardEvent(jfe); }else{ if(type==TriggerType.SCHEDULE ||
	 * type==TriggerType.MANUAL_RECOVER){ //运行成功，发出成功消息
	 * ScheduleInfoLog.info("JobId:"
	 * +jobId+" run success and dispatch the success event"); JobSuccessEvent
	 * jse=new JobSuccessEvent(jobDescriptor.getId(),TriggerType.SCHEDULE);
	 * dispatcher.forwardEvent(jse); } }
	 * 
	 * } }); thread.start();
	 * 
	 * 
	 * }
	 */
	public String getJobId() {
		return jobId;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JobController)) {
			return false;
		}
		JobController jc = (JobController) obj;
		return jobId.equals(jc.getJobId());
	}

	@Override
	public int hashCode() {
		return jobId.hashCode();
	}

	public static class TimerJob implements Job {
		@Override
		public void execute(JobExecutionContext context)
				throws JobExecutionException {
			String jobId = context.getJobDetail().getJobDataMap()
					.getString("jobId");
			Dispatcher dispatcher = (Dispatcher) context.getJobDetail()
					.getJobDataMap().get("dispatcher");
			ScheduleTriggerEvent ste = new ScheduleTriggerEvent(jobId);
			dispatcher.forwardEvent(ste);
		}

	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		JobDescriptor jd = cache.getJobDescriptor();
		if (jd == null) {
			sb.append("JobId:" + jobId + " 查询为null，有异常");
		} else {
			sb.append("JobId:" + jobId).append(
					" auto:" + cache.getJobDescriptor().getAuto());
			sb.append(" dependency:"
					+ cache.getJobDescriptor().getDependencies());
		}
		JobDetail detail = null;
		try {
			detail = context.getScheduler().getJobDetail(jobId, "zeus");
		} catch (SchedulerException e) {
		}
		if (detail == null) {
			sb.append("job not in scheduler");
		} else {
			sb.append("job is in scheduler");
		}
		return sb.toString();
	}
}
