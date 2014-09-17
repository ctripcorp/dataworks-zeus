package com.taobao.zeus.socket.master;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.zeus.broadcast.alarm.MailAlarm;
import com.taobao.zeus.broadcast.alarm.SMSAlarm;
import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.DebugHistory;
import com.taobao.zeus.model.FileDescriptor;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.JobStatus.TriggerType;
import com.taobao.zeus.model.Profile;
import com.taobao.zeus.schedule.mvc.AddJobListener;
import com.taobao.zeus.schedule.mvc.DebugInfoLog;
import com.taobao.zeus.schedule.mvc.DebugListener;
import com.taobao.zeus.schedule.mvc.JobController;
import com.taobao.zeus.schedule.mvc.JobFailListener;
import com.taobao.zeus.schedule.mvc.JobSuccessListener;
import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;
import com.taobao.zeus.schedule.mvc.StopScheduleJobListener;
import com.taobao.zeus.schedule.mvc.ZeusJobException;
import com.taobao.zeus.schedule.mvc.event.DebugFailEvent;
import com.taobao.zeus.schedule.mvc.event.DebugSuccessEvent;
import com.taobao.zeus.schedule.mvc.event.Events;
import com.taobao.zeus.schedule.mvc.event.JobFailedEvent;
import com.taobao.zeus.schedule.mvc.event.JobSuccessEvent;
import com.taobao.zeus.socket.SocketLog;
import com.taobao.zeus.socket.master.MasterWorkerHolder.HeartBeatInfo;
import com.taobao.zeus.socket.master.reqresp.MasterExecuteJob;
import com.taobao.zeus.socket.protocol.Protocol.ExecuteKind;
import com.taobao.zeus.socket.protocol.Protocol.Response;
import com.taobao.zeus.socket.protocol.Protocol.Status;
import com.taobao.zeus.store.GroupBean;
import com.taobao.zeus.store.GroupManagerOld;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistenceOld;
import com.taobao.zeus.util.CronExpParser;
import com.taobao.zeus.util.Environment;
import com.taobao.zeus.util.Tuple;
import com.taobao.zeus.util.ZeusDateTool;

public class Master {

	private MasterContext context;
	private static Logger log = LoggerFactory.getLogger(Master.class);

	public Master(final MasterContext context) {
		this.context = context;
		GroupBean root = context.getGroupManager().getGlobeGroupBean();

		if (Environment.isPrePub()) {
			// 如果是预发环境，添加stop listener，阻止自动调度执行
			context.getDispatcher().addDispatcherListener(
					new StopScheduleJobListener());
		}
		context.getDispatcher().addDispatcherListener(
				new AddJobListener(context, this));
		context.getDispatcher().addDispatcherListener(
				new JobFailListener(context));
		context.getDispatcher().addDispatcherListener(
				new DebugListener(context));
		context.getDispatcher().addDispatcherListener(
				new JobSuccessListener(context));
		//***************2014-09-15 定时扫描JOB表,生成action表********************
		context.getSchedulePool().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Date now = new Date();
				SimpleDateFormat df=new SimpleDateFormat("HH");
				SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
				if(Integer.parseInt(df.format(now)) == 1){
					List<JobPersistenceOld> jobDetails = context.getGroupManagerOld().getAllJobs();
					Map<Long, JobPersistence> actionDetails = new HashMap<Long, JobPersistence>();
					//首先，生成独立任务action
					runScheduleJobToAction(jobDetails, now, df2, actionDetails);
					//其次，生成依赖任务action
					runDependencesJobToAction(jobDetails, actionDetails);
				}
			}
		}, 0, 30, TimeUnit.MINUTES);
		
		//*********************************************************************
		Map<String, JobBean> allJobBeans = root.getAllSubJobBeans();
		for (String id : allJobBeans.keySet()) {
			context.getDispatcher().addController(
					new JobController(context, this, id));
		}

		// 初始化
		context.getDispatcher().forwardEvent(Events.Initialize);
		context.setMaster(this);

		// 定时扫描等待队列
		context.getSchedulePool().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("start scan");
					scan();
					System.out.println("end scan");
				} catch (Exception e) {
					log.error("get job from queue failed!", e);
				}
			}
		}, 0, 3, TimeUnit.SECONDS);
		// 定时扫描worker channel，心跳超过1分钟没有连接就主动断掉
		context.getSchedulePool().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Date now = new Date();
				for (MasterWorkerHolder holder : new ArrayList<MasterWorkerHolder>(
						context.getWorkers().values())) {
					System.out.println("worker start:"+holder.getDebugRunnings().size());
					if (holder.getHeart().timestamp == null
							|| (now.getTime() - holder.getHeart().timestamp
									.getTime()) > 1000 * 60) {
						holder.getChannel().close();
					}
					System.out.println("worker end:"+holder.getDebugRunnings().size());
				}
			}
		}, 30, 30, TimeUnit.SECONDS);
	}

	//获取可用的worker
	private MasterWorkerHolder getRunableWorker() {
		MasterWorkerHolder selectWorker = null;
		Float selectMemRate = null;
		for (MasterWorkerHolder worker : context.getWorkers().values()) {
			HeartBeatInfo heart = worker.getHeart();
			System.out.println("worker a" + "heart :" +heart);
			System.out.println("worker a" + "heart :" +heart.memRate);
			System.out.println("111111111111111111111");
			//if (heart != null && heart.memRate != null && heart.memRate < 0.8) {
			if (heart != null && heart.memRate != null/* && heart.memRate < 0.8*/) {
				System.out.println("22222222222222222222");
				if (selectWorker == null) {
					System.out.println("worker b");
					selectWorker = worker;
					selectMemRate = heart.memRate;
				} else /*if (selectMemRate > heart.memRate) */{
					System.out.println("333333333333333333333333");
					selectWorker = worker;
					selectMemRate = heart.memRate;
					System.out.println("worker c");
				}
			}
			System.out.println("worker d");
		}
		return selectWorker;
	}

	private MasterWorkerHolder getRunableWorker(String host) {
		MasterWorkerHolder selectWorker = null;
		Float selectMemRate = null;
		if (host != null && !"".equals(host)) {
			for (MasterWorkerHolder worker : context.getWorkers().values()) {
				HeartBeatInfo heart = worker.getHeart();
				if (heart != null && heart.memRate != null
						&& heart.memRate < 0.8 && host.equals(heart.host)) {
					if (selectWorker == null) {
						selectWorker = worker;
						selectMemRate = heart.memRate;
					} else if (selectMemRate > heart.memRate) {
						selectWorker = worker;
						selectMemRate = heart.memRate;
					}
				}
			}
			return selectWorker;
		}

		else {
			return this.getRunableWorker();
		}

	}

	//扫描可用的worker，给worker分配JOB任务
	private void scan() {

		if (!context.getQueue().isEmpty()) {
			final JobElement e = context.getQueue().poll();
			MasterWorkerHolder selectWorker = getRunableWorker(e.getHost());
			if (selectWorker == null) {
				context.getQueue().offer(e);
			} else {
				runScheduleJob(selectWorker, e.getJobID());
			}
		}
		
		if (!context.getManualQueue().isEmpty()) {
			final JobElement e = context.getManualQueue().poll();
			MasterWorkerHolder selectWorker = getRunableWorker(e.getHost());
			if (selectWorker == null) {
				context.getManualQueue().offer(e);
			} else {
				runManualJob(selectWorker, e.getJobID());
			}
		}
		
		if (!context.getDebugQueue().isEmpty()) {
			System.out.println("debug queue :" +context.getDebugQueue().size() );
			final JobElement e = context.getDebugQueue().poll();
			MasterWorkerHolder selectWorker = getRunableWorker(e.getHost());
			System.out.println("selectWorker :" +selectWorker+" host :"+e.getHost());
			if (selectWorker == null) {
				context.getDebugQueue().offer(e);
			} else {
				runDebugJob(selectWorker, e.getJobID());
			}
		}
		
		// 检测任务超时
		checkTimeOver();
	}

	private void runDebugJob(MasterWorkerHolder selectWorker, final String jobID) {
		final MasterWorkerHolder w = selectWorker;
		//final JobElement debugId = context.getDebugQueue().poll();
		SocketLog.info("master scan and poll debugId=" + jobID
				+ " and run!");

		new Thread() {
			@Override
			public void run() {
				DebugHistory history = context.getDebugHistoryManager()
						.findDebugHistory(jobID);
				history.getLog().appendZeus(
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date()) + " 开始运行");
				context.getDebugHistoryManager().updateDebugHistoryLog(
						jobID, history.getLog().getContent());
				Exception exception = null;
				Response resp = null;
				try {
					Future<Response> f = new MasterExecuteJob().executeJob(
							context, w, ExecuteKind.DebugKind,
							jobID);
					resp = f.get();
				} catch (Exception e) {
					exception = e;
					DebugInfoLog.error(
							String.format("debugId:%s run failed",
									jobID), e);
				}
				boolean success = resp.getStatus() == Status.OK ? true : false;

				if (!success) {
					// 运行失败，更新失败状态，发出失败消息
					if (exception != null) {
						exception = new ZeusException(String.format(
								"fileId:%s run failed ", history.getFileId()),
								exception);
					} else {
						exception = new ZeusException(String.format(
								"fileId:%s run failed ", history.getFileId()));
					}
					DebugInfoLog.info("debugId:" + jobID + " run fail ");
					history = context.getDebugHistoryManager()
							.findDebugHistory(jobID);
					DebugFailEvent jfe = new DebugFailEvent(
							history.getFileId(), history, exception);
					context.getDispatcher().forwardEvent(jfe);
				} else {
					// 运行成功，发出成功消息
					DebugInfoLog.info("debugId:" + jobID + " run success");
					DebugSuccessEvent dse = new DebugSuccessEvent(
							history.getFileId(), history);
					context.getDispatcher().forwardEvent(dse);
				}
			}
		}.start();
	}

	private void runManualJob(MasterWorkerHolder selectWorker,final String jobID) {
		final MasterWorkerHolder w = selectWorker;
		//final JobElement historyId = context.getManualQueue().poll();
		SocketLog.info("master scan and poll historyId=" + jobID
				+ " and run!");
		new Thread() {
			@Override
			public void run() {
				JobHistory history = context.getJobHistoryManager()
						.findJobHistory(jobID);
				history.getLog().appendZeus(
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date()) + " 开始运行");
				context.getJobHistoryManager().updateJobHistoryLog(
						jobID, history.getLog().getContent());
				Exception exception = null;
				Response resp = null;
				try {
					Future<Response> f = new MasterExecuteJob().executeJob(
							context, w, ExecuteKind.ManualKind,
							jobID);
					resp = f.get();
				} catch (Exception e) {
					exception = e;
					ScheduleInfoLog.error("JobId:" + history.getJobId()
							+ " run failed", e);
				}
				boolean success = resp.getStatus() == Status.OK ? true : false;

				if (!success) {
					// 运行失败，更新失败状态，发出失败消息
					ZeusJobException jobException = null;
					if (exception != null) {
						jobException = new ZeusJobException(history.getJobId(),
								String.format("JobId:%s run failed ",
										history.getJobId()), exception);
					} else {
						jobException = new ZeusJobException(history.getJobId(),
								String.format("JobId:%s run failed ",
										history.getJobId()));
					}
					ScheduleInfoLog.info("jobId:" + history.getJobId()
							+ " run fail ");
					history = context.getJobHistoryManager().findJobHistory(
							jobID);
					JobFailedEvent jfe = new JobFailedEvent(history.getJobId(),
							history.getTriggerType(), history, jobException);
					context.getDispatcher().forwardEvent(jfe);
				} else {
					// 运行成功，发出成功消息
					ScheduleInfoLog.info("manual jobId::" + history.getJobId()
							+ " run success");
					JobSuccessEvent jse = new JobSuccessEvent(
							history.getJobId(), history.getTriggerType(),
							jobID);
					context.getDispatcher().forwardEvent(jse);
				}
			};
		}.start();
	}

	private void runScheduleJob(MasterWorkerHolder selectWorker,final String jobID) {
		final MasterWorkerHolder w = selectWorker;
		//final JobElement jobId = context.getQueue().poll();
		SocketLog.info("master scan and poll jobId=" + jobID + " and run!");
		new Thread() {
			@Override
			public void run() {
				// 先根据任务ID，查询出任务上次执行的历史记录（jobID->historyid->JobHistory)
				JobHistory his = context.getJobHistoryManager().findJobHistory(
						context.getGroupManager()
								.getJobStatus(jobID).getHistoryId());
				TriggerType type = his.getTriggerType();
				ScheduleInfoLog.info("JobId:" + jobID + " run start");
				his.getLog().appendZeus(
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date()) + " 开始运行");
				context.getJobHistoryManager().updateJobHistoryLog(his.getId(),
						his.getLog().getContent());
				Exception exception = null;
				Response resp = null;
				try {
					Future<Response> f = new MasterExecuteJob().executeJob(
							context, w, ExecuteKind.ScheduleKind, his.getId());
					resp = f.get();
				} catch (Exception e) {
					exception = e;
					ScheduleInfoLog.error(
							String.format("JobId:%s run failed", jobID), e);
				}
				boolean success = resp.getStatus() == Status.OK ? true : false;

				JobStatus jobstatus = context.getGroupManager().getJobStatus(
						jobID);
				jobstatus
						.setStatus(com.taobao.zeus.model.JobStatus.Status.WAIT);
				if (success
						&& (his.getTriggerType() == TriggerType.SCHEDULE || his
								.getTriggerType() == TriggerType.MANUAL_RECOVER)) {
					ScheduleInfoLog.info("JobId:" + jobID
							+ " clear ready dependency");
					jobstatus.setReadyDependency(new HashMap<String, String>());
				}
				context.getGroupManager().updateJobStatus(jobstatus);

				if (!success) {
					// 运行失败，更新失败状态，发出失败消息
					ZeusJobException jobException = null;
					if (exception != null) {
						jobException = new ZeusJobException(jobID,
								String.format("JobId:%s run failed ",
										jobID), exception);
					} else {
						jobException = new ZeusJobException(jobID,
								String.format("JobId:%s run failed ",
										jobID));
					}
					ScheduleInfoLog.info("JobId:" + jobID
							+ " run fail and dispatch the fail event");
					JobFailedEvent jfe = new JobFailedEvent(jobID,
							type, context.getJobHistoryManager()
									.findJobHistory(his.getId()), jobException);
					context.getDispatcher().forwardEvent(jfe);
				} else {
					// 运行成功，发出成功消息
					ScheduleInfoLog.info("JobId:" + jobID
							+ " run success and dispatch the success event");
					JobSuccessEvent jse = new JobSuccessEvent(jobID,
							his.getTriggerType(), his.getId());
					jse.setStatisEndTime(his.getStatisEndTime());
					context.getDispatcher().forwardEvent(jse);
				}
			}
		}.start();
	}

	/**
	 * 检查任务超时
	 */
	private void checkTimeOver() {
		for (MasterWorkerHolder w : context.getWorkers().values()) {
			checkScheduleTimeOver(w);
			checkManualTimeOver(w);
			checkDebugTimeOver(w);
		}
	}

	private void checkDebugTimeOver(MasterWorkerHolder w) {
		for (Map.Entry<String, Boolean> entry : w.getDebugRunnings().entrySet()) {
			if (entry.getValue() != null && entry.getValue()) {
				continue;
			}
			String historyId = entry.getKey();
			DebugHistory his = context.getDebugHistoryManager()
					.findDebugHistory(historyId);
			long maxTime;
			FileDescriptor fd;
			try {
				fd = context.getFileManager().getFile(his.getFileId());
				Profile pf = context.getProfileManager().findByUid(
						fd.getOwner());
				String maxTimeString = pf.getHadoopConf().get(
						"zeus.job.maxtime");
				if (maxTimeString == null || maxTimeString.trim().isEmpty()) {
					continue;
				}
				maxTime = Long.parseLong(maxTimeString);

				if (maxTime < 0) {
					continue;
				}
			} catch (Exception e) {
				continue;
			}
			long runTime = (System.currentTimeMillis() - his.getStartTime()
					.getTime()) / 1000 / 60;
			if (runTime > maxTime) {
				if (timeOverAlarm(null, fd, runTime, maxTime, 2)) {
					w.getDebugRunnings().replace(historyId, false, true);
				}
			}
		}
	}

	private void checkManualTimeOver(MasterWorkerHolder w) {
		for (Map.Entry<String, Boolean> entry : w.getManualRunnings()
				.entrySet()) {
			if (entry.getValue() != null && entry.getValue()) {
				continue;
			}
			String historyId = entry.getKey();
			JobHistory his = context.getJobHistoryManager().findJobHistory(
					historyId);
			long maxTime;
			try {
				JobDescriptor jd = context.getGroupManager()
						.getJobDescriptor(his.getJobId()).getX();
				String maxTimeString = jd.getProperties().get(
						"zeus.job.maxtime");
				if (maxTimeString == null || maxTimeString.trim().isEmpty()) {
					continue;
				}
				maxTime = Long.parseLong(maxTimeString);

				if (maxTime < 0) {
					continue;
				}
			} catch (Exception e) {
				continue;
			}
			long runTime = (System.currentTimeMillis() - his.getStartTime()
					.getTime()) / 1000 / 60;
			if (runTime > maxTime) {
				if (timeOverAlarm(his, null, runTime, maxTime, 1)) {
					w.getManualRunnings().replace(historyId, false, true);
				}
			}
		}
	}

	private void checkScheduleTimeOver(MasterWorkerHolder w) {
		for (Map.Entry<String, Boolean> entry : w.getRunnings().entrySet()) {
			if (entry.getValue() != null && entry.getValue()) {
				continue;
			}
			String jobId = entry.getKey();
			JobDescriptor jd = context.getGroupManager()
					.getJobDescriptor(jobId).getX();
			String maxTimeString = jd.getProperties().get("zeus.job.maxtime");
			long maxTime;
			try {
				if (maxTimeString == null || maxTimeString.trim().isEmpty()) {
					continue;
				}
				maxTime = Long.parseLong(maxTimeString);

				if (maxTime < 0) {
					continue;
				}
			} catch (Exception e) {
				continue;
			}

			JobHistory his = context.getJobHistoryManager().findJobHistory(
					context.getGroupManager().getJobStatus(jobId)
							.getHistoryId());
			long runTime = (System.currentTimeMillis() - his.getStartTime()
					.getTime()) / 1000 / 60;
			if (runTime > maxTime) {
				if (timeOverAlarm(his, null, runTime, maxTime, 0)) {
					w.getRunnings().replace(jobId, false, true);
				}
			}
		}
	}

	private boolean timeOverAlarm(final JobHistory his, FileDescriptor fd,
			long runTime, long maxTime, int type) {
		final MailAlarm mailAlarm = (MailAlarm) context.getApplicationContext()
				.getBean("mailAlarm");
		SMSAlarm smsAlarm = (SMSAlarm) context.getApplicationContext().getBean(
				"smsAlarm");

		final StringBuffer title = new StringBuffer("宙斯任务超时[");
		switch (type) {
		case 0:
			title.append("自动调度").append("] jobID=").append(his.getJobId());
			break;
		case 1:
			title.append("手动调度").append("] jobID=").append(his.getJobId());
			break;
		case 2:
			title.append("调试任务").append("] 脚本名称：").append(fd.getName());
		}
		final StringBuffer content = new StringBuffer(title);
		content.append("\n已经运行时间：").append(runTime).append("分钟")
				.append("\n设置最大运行时间：").append(maxTime).append("分钟")
				.append("\n详情请登录zeus系统查看：http://zeus.taobao.com:9999");
		try {
			if (type == 2) {
				// 此处可以发送IM消息
			} else {
				// 此处可以发送IM消息
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(6000);
							mailAlarm
									.alarm(his.getId(),
											title.toString(),
											content.toString()
													.replace("\n", "<br/>")
													.replace(
															"http://zeus.taobao.com:9999",
															"<a href='http://zeus.taobao.com:9999'>http://zeus.taobao.com:9999</a>"));
						} catch (Exception e) {
							log.error("send run timeover mail alarm failed", e);
						}
					}
				}.start();
				if (type == 0) {
					Calendar now = Calendar.getInstance();
					int hour = now.get(Calendar.HOUR_OF_DAY);
					int day = now.get(Calendar.DAY_OF_WEEK);
					if (day == Calendar.SATURDAY || day == Calendar.SUNDAY
							|| hour < 9 || hour > 18) {
						smsAlarm.alarm(his.getId(), title.toString(),
								content.toString(), null);
					}
				}
			}
			return true;
		} catch (Exception e) {
			log.error("send run timeover alarm failed", e);
			return false;
		}
	}

	public void workerDisconnectProcess(Channel channel) {
		MasterWorkerHolder holder = context.getWorkers().get(channel);
		if (holder != null) {
			context.getWorkers().remove(channel);
			final List<JobHistory> hiss = new ArrayList<JobHistory>();
			Map<String, Tuple<JobDescriptor, JobStatus>> map = context
					.getGroupManager().getJobDescriptor(
							holder.getRunnings().keySet());
			for (String key : map.keySet()) {
				JobStatus js = map.get(key).getY();
				if (js.getHistoryId() != null) {
					hiss.add(context.getJobHistoryManager().findJobHistory(
							js.getHistoryId()));
				}
				js.setStatus(com.taobao.zeus.model.JobStatus.Status.FAILED);
				context.getGroupManager().updateJobStatus(js);
			}
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
					}
					for (JobHistory his : hiss) {
						String jobId = his.getJobId();
						JobHistory history = new JobHistory();
						history.setJobId(jobId);
						history.setTriggerType(his.getTriggerType());
						history.setIllustrate("worker断线，重新跑任务");
						history.setOperator(his.getOperator());
						context.getJobHistoryManager().addJobHistory(history);
						Master.this.run(history);
					}
				};
			}.start();

		}
	}

	public void debug(DebugHistory debug) {
		JobElement element = new JobElement(debug.getId(), debug.getHost());
		debug.setStatus(com.taobao.zeus.model.JobStatus.Status.RUNNING);
		debug.setStartTime(new Date());
		context.getDebugHistoryManager().updateDebugHistory(debug);
		debug.getLog().appendZeus(
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
						+ " 进入任务队列");
		context.getDebugHistoryManager().updateDebugHistoryLog(debug.getId(),
				debug.getLog().getContent());
		context.getDebugQueue().offer(element);
System.out.println("offer debug queue :" +context.getDebugQueue().size()+ "element :"+element.getJobID());
	}

	public JobHistory run(JobHistory history) {
		String jobId = history.getJobId();
		JobElement element = new JobElement(jobId, history.getExecuteHost());
		history.setStatus(com.taobao.zeus.model.JobStatus.Status.RUNNING);
		if (history.getTriggerType() == TriggerType.MANUAL_RECOVER) {
			for (JobElement e : new ArrayList<JobElement>(context.getQueue())) {
				if (e.getJobID().equals(jobId)) {
					history.getLog().appendZeus("已经在队列中，无法再次运行");
					history.setStartTime(new Date());
					history.setEndTime(new Date());
					history.setStatus(com.taobao.zeus.model.JobStatus.Status.FAILED);
					break;
				}
			}
			for (Channel key : context.getWorkers().keySet()) {
				MasterWorkerHolder worker = context.getWorkers().get(key);
				if (worker.getRunnings().containsKey(jobId)) {
					history.getLog().appendZeus("已经在运行中，无法再次运行");
					history.setStartTime(new Date());
					history.setEndTime(new Date());
					history.setStatus(com.taobao.zeus.model.JobStatus.Status.FAILED);
					break;
				}
			}
		}

		if (history.getStatus() == com.taobao.zeus.model.JobStatus.Status.RUNNING) {
			history.getLog().appendZeus(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(new Date()) + " 进入任务队列");
			context.getJobHistoryManager().updateJobHistoryLog(history.getId(),
					history.getLog().getContent());
			if (history.getTriggerType() == TriggerType.MANUAL) {
				element.setJobID(history.getId());
				context.getManualQueue().offer(element);
			} else {
				JobStatus js = context.getGroupManager().getJobStatus(
						history.getJobId());
				js.setStatus(com.taobao.zeus.model.JobStatus.Status.RUNNING);
				js.setHistoryId(history.getId());
				context.getGroupManager().updateJobStatus(js);
				context.getQueue().offer(element);
			}
		}
		context.getJobHistoryManager().updateJobHistory(history);
		context.getJobHistoryManager().updateJobHistoryLog(history.getId(),
				history.getLog().getContent());
		return history;
	}
	
	//将定时任务生成action
	public void runScheduleJobToAction(List<JobPersistenceOld> jobDetails, Date now, SimpleDateFormat df2, Map<Long, JobPersistence> actionDetails){
		for(JobPersistenceOld jobDetail : jobDetails){
			//ScheduleType: 0 独立任务; 1依赖任务
			if(jobDetail.getScheduleType()==0){
				String jobCronExpression = jobDetail.getCronExpression();
				String cronDate= df2.format(now);
				List<String> lTime = new ArrayList<String>();
				if(jobCronExpression != null && jobCronExpression.trim().length() > 0){
					//定时调度
					boolean isCronExp = false;
					try{
						isCronExp = CronExpParser.Parser(jobCronExpression, cronDate, lTime);
					}catch(Exception ex){
						isCronExp = false;
					}
					if (!isCronExp) {
						log.error("无法生成Cron表达式：日期," + cronDate + ";不符合规则cron表达式：" + jobCronExpression);
					}
					for (int i = 0; i < lTime.size(); i++) {
						String actionDateStr = ZeusDateTool.StringToDateStr(lTime.get(i), "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss");
						String actionCronExpr = ZeusDateTool.StringToDateStr(lTime.get(i), "yyyy-MM-dd HH:mm:ss", "s m H d M") + " ?";
						
						JobPersistence actionPer = new JobPersistence();
						actionPer.setId(Long.parseLong(actionDateStr)*10000+jobDetail.getId());//update action id
						actionPer.setToJobId(jobDetail.getId());
						actionPer.setAuto(jobDetail.getAuto());
						actionPer.setConfigs(jobDetail.getConfigs());
						actionPer.setCronExpression(actionCronExpr);//update action cron expression
						actionPer.setCycle(jobDetail.getCycle());
						String jobDependencies = jobDetail.getDependencies();
						actionPer.setDependencies(jobDependencies);
						actionPer.setJobDependencies(jobDependencies);
						actionPer.setDescr(jobDetail.getDescr());
						actionPer.setGmtCreate(jobDetail.getGmtCreate());
						actionPer.setGmtModified(jobDetail.getGmtModified());
						actionPer.setGroupId(jobDetail.getGroupId());
						actionPer.setHistoryId(jobDetail.getHistoryId());
						actionPer.setHost(jobDetail.getHost());
						actionPer.setLastEndTime(jobDetail.getLastEndTime());
						actionPer.setLastResult(jobDetail.getLastResult());
						actionPer.setName(jobDetail.getName());
						actionPer.setOffset(jobDetail.getOffset());
						actionPer.setOwner(jobDetail.getOwner());
						actionPer.setPostProcessers(jobDetail.getPostProcessers());
						actionPer.setPreProcessers(jobDetail.getPreProcessers());
						actionPer.setReadyDependency(jobDetail.getReadyDependency());
						actionPer.setResources(jobDetail.getResources());
						actionPer.setRunType(jobDetail.getRunType());
						actionPer.setScheduleType(jobDetail.getScheduleType());
						actionPer.setScript(jobDetail.getScript());
						actionPer.setStartTime(jobDetail.getStartTime());
						actionPer.setStartTimestamp(jobDetail.getStartTimestamp());
						actionPer.setStatisStartTime(jobDetail.getStatisStartTime());
						actionPer.setStatisEndTime(jobDetail.getStatisEndTime());
						actionPer.setStatus(jobDetail.getStatus());
						actionPer.setTimezone(jobDetail.getTimezone());
						try {
							context.getGroupManager().saveJob(actionPer);
							actionDetails.put(actionPer.getId(),actionPer);
						} catch (ZeusException e) {
							log.error("独立JobId:" + jobDetail.getId() + " 生成Action" +actionPer.getId() + "失败", e);
						}
					}
				}
			}
		}
	}
	
	//将依赖任务生成action
	public void runDependencesJobToAction(List<JobPersistenceOld> jobDetails, Map<Long, JobPersistence> actionDetails){
		int noCompleteCount = 0;
		for(JobPersistenceOld jobDetail : jobDetails){
			//ScheduleType: 0 独立任务; 1依赖任务
			if(jobDetail.getScheduleType()==1){
				String jobDependencies = jobDetail.getDependencies();
				String actionDependencies = "";
				if(jobDependencies != null && jobDependencies.trim().length()>0){

					//计算这些依赖任务的版本数
					Map<String,List<JobPersistence>> dependActionList = new HashMap<String,List<JobPersistence>>();
					String[] dependStrs = jobDependencies.split(",");
					for(String deps : dependStrs){
						List<JobPersistence> dependActions = new ArrayList<JobPersistence>();
						Iterator<JobPersistence> actionIt = actionDetails.values().iterator();
						while(actionIt.hasNext()){
							JobPersistence action = actionIt.next();
							if(action.getToJobId().toString().equals(deps)){
								dependActions.add(action);
							}
						}
						dependActionList.put(deps, dependActions);
					}
					//判断是否有未完成的
					boolean isComplete = true;
//					Long actionId = 0L;
					String actionMostDeps = "";
					for(String deps : dependStrs){
						if(jobDetail.getCycle().trim().equals("day") || jobDetail.getCycle().trim().equals("hour")){
							if(dependActionList.get(deps).size()==0){
								isComplete = false;
								noCompleteCount ++;
								break;
							}
							if(actionMostDeps.trim().length()==0){
								actionMostDeps = deps;
							}
							if(dependActionList.get(deps).size()>dependActionList.get(actionMostDeps).size()){
								actionMostDeps = deps;
							}
						}
					}
					if(!isComplete){
						break;
					}else{
						List<JobPersistence> actions = dependActionList.get(actionMostDeps);
						if(actions != null && actions.size()>0){
							for(JobPersistence actionModel : actions){
								actionDependencies = String.valueOf((actionModel.getId()/10000)*10000 + actionModel.getToJobId());
								for(String deps : dependStrs){
									if(!deps.equals(actionMostDeps)){
										Long actionOtherId = actionModel.getId();
										List<JobPersistence> actionOthers = dependActionList.get(deps);
										int i = 0;
										for(JobPersistence actionOtherModel : actionOthers){
											if(i==0){
												if(actionOtherId<actionOtherModel.getId()){
													actionOtherId = actionOtherModel.getId();
													i++;
												}
											}else{
												if(actionOtherModel.getId()<actionOtherId && actionOtherModel.getId()>actionModel.getId()){
													actionOtherId = actionOtherModel.getId();
												}
											}
										}
										if(actionDependencies.trim().length()>0){
											actionDependencies += ",";
										}
										actionDependencies += String.valueOf((actionOtherId/10000)*10000 + Long.parseLong(deps));
									}
								}
								//保存多版本的action
								JobPersistence actionPer = new JobPersistence();
								actionPer.setId((actionModel.getId()/10000)*10000+jobDetail.getId());//update action id
								actionPer.setToJobId(jobDetail.getId());
								actionPer.setAuto(jobDetail.getAuto());
								actionPer.setConfigs(jobDetail.getConfigs());
								actionPer.setCronExpression(jobDetail.getCronExpression());//update action cron expression
								actionPer.setCycle(jobDetail.getCycle());
								actionPer.setDependencies(actionDependencies);
								actionPer.setJobDependencies(jobDependencies);
								actionPer.setDescr(jobDetail.getDescr());
								actionPer.setGmtCreate(jobDetail.getGmtCreate());
								actionPer.setGmtModified(jobDetail.getGmtModified());
								actionPer.setGroupId(jobDetail.getGroupId());
								actionPer.setHistoryId(jobDetail.getHistoryId());
								actionPer.setHost(jobDetail.getHost());
								actionPer.setLastEndTime(jobDetail.getLastEndTime());
								actionPer.setLastResult(jobDetail.getLastResult());
								actionPer.setName(jobDetail.getName());
								actionPer.setOffset(jobDetail.getOffset());
								actionPer.setOwner(jobDetail.getOwner());
								actionPer.setPostProcessers(jobDetail.getPostProcessers());
								actionPer.setPreProcessers(jobDetail.getPreProcessers());
								actionPer.setReadyDependency(jobDetail.getReadyDependency());
								actionPer.setResources(jobDetail.getResources());
								actionPer.setRunType(jobDetail.getRunType());
								actionPer.setScheduleType(jobDetail.getScheduleType());
								actionPer.setScript(jobDetail.getScript());
								actionPer.setStartTime(jobDetail.getStartTime());
								actionPer.setStartTimestamp(jobDetail.getStartTimestamp());
								actionPer.setStatisStartTime(jobDetail.getStatisStartTime());
								actionPer.setStatisEndTime(jobDetail.getStatisEndTime());
								actionPer.setStatus(jobDetail.getStatus());
								actionPer.setTimezone(jobDetail.getTimezone());
								try {
									context.getGroupManager().saveJob(actionPer);
									actionDetails.put(actionPer.getId(),actionPer);
								} catch (ZeusException e) {
									log.error("依赖JobId:" + jobDetail.getId() + " 生成Action" +actionPer.getId() + "失败", e);
								}
							}
						}
					}
				}
			}
		}
		if(noCompleteCount>0){
			runDependencesJobToAction(jobDetails, actionDetails);
		}
	}
}
