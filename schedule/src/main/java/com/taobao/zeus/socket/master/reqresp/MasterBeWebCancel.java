package com.taobao.zeus.socket.master.reqresp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;

import com.taobao.zeus.model.DebugHistory;
import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.JobStatus.TriggerType;
import com.taobao.zeus.socket.SocketLog;
import com.taobao.zeus.socket.master.JobElement;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.socket.master.MasterWorkerHolder;
import com.taobao.zeus.socket.protocol.Protocol.ExecuteKind;
import com.taobao.zeus.socket.protocol.Protocol.Response;
import com.taobao.zeus.socket.protocol.Protocol.Status;
import com.taobao.zeus.socket.protocol.Protocol.WebRequest;
import com.taobao.zeus.socket.protocol.Protocol.WebResponse;

public class MasterBeWebCancel {
	public WebResponse beWebCancel(MasterContext context, WebRequest req) {
		// 判断job是否在运行中，或者在等待队列
		// 如果在运行中，执行取消命令，如果在等待中，从等待队列删除
		// 如果不在，抛出异常，job没有在运行中
		if (req.getEk() == ExecuteKind.ScheduleKind) {
			return processScheduleCancel(context, req);
		} else if (req.getEk() == ExecuteKind.ManualKind) {
			return processManualCancel(context, req);
		} else if (req.getEk() == ExecuteKind.DebugKind) {
			return processDebugCancel(context, req);
		}
		return null;
	}

	private WebResponse processManualCancel(MasterContext context,
			WebRequest req) {
		WebResponse ret = null;
		String historyId = req.getId();
		JobHistory history = context.getJobHistoryManager().findJobHistory(
				historyId);
		SocketLog.info("receive web cancel request,rid=" + req.getRid()
				+ ",jobId=" + history.getJobId());
		String jobId = history.getId();
		for (JobElement e : new ArrayList<JobElement>(context.getManualQueue())) {
			if (e.getJobID().equals(historyId)) {
				if (context.getManualQueue().remove(e.getJobID())) {
					ret = WebResponse.newBuilder().setRid(req.getRid())
							.setOperate(req.getOperate()).setStatus(Status.OK)
							.build();
					history.getLog().appendZeus("任务被取消");
					context.getJobHistoryManager().updateJobHistoryLog(
							history.getId(), history.getLog().getContent());
					break;
				}
			}
		}
		if (history.getTriggerType() == TriggerType.MANUAL) {
			for (Channel key : new HashSet<Channel>(context.getWorkers()
					.keySet())) {
				MasterWorkerHolder worker = context.getWorkers().get(key);
				if (worker.getManualRunnings().containsKey(historyId)) {
					Future<Response> f = new MasterCancelJob().cancel(context,
							worker.getChannel(), ExecuteKind.ManualKind,
							historyId);
					worker.getManualRunnings().remove(historyId);
					try {
						f.get(30, TimeUnit.SECONDS);
					} catch (Exception e) {
					}
					ret = WebResponse.newBuilder().setRid(req.getRid())
							.setOperate(req.getOperate()).setStatus(Status.OK)
							.build();
					SocketLog.info("send web cancel response,rid="
							+ req.getRid() + ",jobId=" + jobId);
				}
			}
		}

		if (ret == null) {
			// 找不到job，失败
			ret = WebResponse.newBuilder().setRid(req.getRid())
					.setOperate(req.getOperate()).setStatus(Status.ERROR)
					.setErrorText("Mannual任务中找不到匹配的job("+history.getJobId()+","+history.getId()+")，无法执行取消命令").build();
		}

		history = context.getJobHistoryManager().findJobHistory(historyId);
		history.setEndTime(new Date());
		history.setStatus(com.taobao.zeus.model.JobStatus.Status.FAILED);
		context.getJobHistoryManager().updateJobHistory(history);

		return ret;
	}

	private WebResponse processDebugCancel(MasterContext context, WebRequest req) {
		WebResponse ret = null;
		String debugId = req.getId();
		DebugHistory history = context.getDebugHistoryManager()
				.findDebugHistory(debugId);
		SocketLog.info("receive web debug cancel request,rid=" + req.getRid()
				+ ",debugId=" + debugId);
		for (JobElement e : new ArrayList<JobElement>(context.getDebugQueue())) {
			if (e.getJobID().equals(debugId)) {
				if (context.getDebugQueue().remove(debugId)) {
					ret = WebResponse.newBuilder().setRid(req.getRid())
							.setOperate(req.getOperate()).setStatus(Status.OK)
							.build();
					history.getLog().appendZeus("任务被取消");
					context.getDebugHistoryManager().updateDebugHistoryLog(
							history.getId(), history.getLog().getContent());
					break;
				}
			}
		}
		for (Channel key : new HashSet<Channel>(context.getWorkers().keySet())) {
			MasterWorkerHolder worker = context.getWorkers().get(key);
			if (worker.getDebugRunnings().containsKey(debugId)) {
				Future<Response> f = new MasterCancelJob().cancel(context,
						worker.getChannel(), ExecuteKind.DebugKind, debugId);
				worker.getDebugRunnings().remove(debugId);
				try {
					f.get(10, TimeUnit.SECONDS);
				} catch (Exception e) {
				}
				ret = WebResponse.newBuilder().setRid(req.getRid())
						.setOperate(req.getOperate()).setStatus(Status.OK)
						.build();
				SocketLog.info("send web debug cancel response,rid="
						+ req.getRid() + ",debugId=" + debugId);
			}
		}
		if (ret == null) {
			// 找不到job，失败
			ret = WebResponse.newBuilder().setRid(req.getRid())
					.setOperate(req.getOperate()).setStatus(Status.ERROR)
					.setErrorText("Debug任务中找不到匹配的job("+history.getFileId()+","+history.getId()+")，无法执行取消命令").build();
		}
		// 再查一次，获取最新数据
		history = context.getDebugHistoryManager().findDebugHistory(debugId);
		history.setEndTime(new Date());
		history.setStatus(com.taobao.zeus.model.JobStatus.Status.FAILED);
		context.getDebugHistoryManager().updateDebugHistory(history);
		return ret;
	}

	private WebResponse processScheduleCancel(MasterContext context,
			WebRequest req) {
		WebResponse ret = null;
		String historyId = req.getId();
		JobHistory history = context.getJobHistoryManager().findJobHistory(
				historyId);
		SocketLog.info("receive web cancel request,rid=" + req.getRid()
				+ ",jobId=" + history.getJobId());
		String jobId = history.getJobId();
		for (JobElement e : new ArrayList<JobElement>(context.getQueue())) {
			if (e.getJobID().equals(jobId)) {
				if (context.getQueue().remove(e.getJobID())) {
					ret = WebResponse.newBuilder().setRid(req.getRid())
							.setOperate(req.getOperate()).setStatus(Status.OK)
							.build();
					history.getLog().appendZeus("任务被取消");
					context.getJobHistoryManager().updateJobHistoryLog(
							history.getId(), history.getLog().getContent());
					break;
				}
			}
		}
		for (Channel key : new HashSet<Channel>(context.getWorkers().keySet())) {
			MasterWorkerHolder worker = context.getWorkers().get(key);
			if (worker.getRunnings().containsKey(jobId)) {
				Future<Response> f = new MasterCancelJob().cancel(context,
						worker.getChannel(), ExecuteKind.ScheduleKind,
						historyId);
				worker.getRunnings().remove(jobId);
				try {
					f.get(10, TimeUnit.SECONDS);
				} catch (Exception e) {
				}
				ret = WebResponse.newBuilder().setRid(req.getRid())
						.setOperate(req.getOperate()).setStatus(Status.OK)
						.build();
				SocketLog.info("send web cancel response,rid=" + req.getRid()
						+ ",jobId=" + jobId);
			}
		}

		if (ret == null) {
			// 数据库设置状态
			JobStatus js = context.getGroupManager().getJobStatus(jobId);
			js.setStatus(com.taobao.zeus.model.JobStatus.Status.WAIT);
			js.setHistoryId(null);
			context.getGroupManager().updateJobStatus(js);
			// 找不到job，失败
			ret = WebResponse.newBuilder().setRid(req.getRid())
					.setOperate(req.getOperate()).setStatus(Status.ERROR)
					.setErrorText("Schedule任务中找不到匹配的job("+history.getJobId()+","+history.getId()+")，无法执行取消命令").build();
		}
		history = context.getJobHistoryManager().findJobHistory(historyId);
		history.setEndTime(new Date());
		history.setStatus(com.taobao.zeus.model.JobStatus.Status.FAILED);
		context.getJobHistoryManager().updateJobHistory(history);
		return ret;
	}
}
