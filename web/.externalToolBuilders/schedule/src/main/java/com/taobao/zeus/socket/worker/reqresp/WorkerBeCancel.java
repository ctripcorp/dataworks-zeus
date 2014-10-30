package com.taobao.zeus.socket.worker.reqresp;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.google.protobuf.InvalidProtocolBufferException;
import com.taobao.zeus.model.DebugHistory;
import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.socket.SocketLog;
import com.taobao.zeus.socket.protocol.Protocol.CancelMessage;
import com.taobao.zeus.socket.protocol.Protocol.ExecuteKind;
import com.taobao.zeus.socket.protocol.Protocol.Operate;
import com.taobao.zeus.socket.protocol.Protocol.Request;
import com.taobao.zeus.socket.protocol.Protocol.Response;
import com.taobao.zeus.socket.protocol.Protocol.Status;
import com.taobao.zeus.socket.worker.WorkerContext;

public class WorkerBeCancel {

	public Future<Response> execute(final WorkerContext context,final Request req){
		try {
			CancelMessage cm=CancelMessage.newBuilder().mergeFrom(req.getBody()).build();
			if(cm.getEk()==ExecuteKind.DebugKind){
				return cancelDebug(context, req,cm.getId());
			}else if(cm.getEk()==ExecuteKind.ManualKind){
				return cancelManual(context, req, cm.getId());
			}else if(cm.getEk()==ExecuteKind.ScheduleKind){
				return cancelSchedule(context, req,cm.getId());
			}
		} catch (InvalidProtocolBufferException e) {
		}
		return null;
	}
	
	public Future<Response> cancelManual(final WorkerContext context,final Request req,final String historyId){
		// 查找该job是否在运行中，如果不在，响应ERROR
		// 如果在，执行取消操作，等待操作结束后，响应OK
		JobHistory history=context.getJobHistoryManager().findJobHistory(historyId);
		final String jobId=history.getJobId();
		SocketLog.info("receive master cancel request,rid="+req.getRid()+",jobId="+jobId);
		if(!context.getManualRunnings().containsKey(historyId)){
			return context.getThreadPool().submit(new Callable<Response>() {
				public Response call() throws Exception {
					return Response.newBuilder().setRid(req.getRid()).setOperate(Operate.Cancel).setStatus(Status.ERROR)
					.setErrorText("运行任务中查无此任务").build();
				}
			});
		}
		return context.getThreadPool().submit(new Callable<Response>() {
			public Response call() throws Exception {
				context.getClientWorker().cancelManualJob(historyId);
				Response resp=Response.newBuilder().setRid(req.getRid()).setOperate(Operate.Cancel).setStatus(Status.OK).build();
				return resp;
			}
		});
	}
	
	public Future<Response> cancelSchedule(final WorkerContext context,final Request req,final String historyId){
		// 查找该job是否在运行中，如果不在，响应ERROR
		// 如果在，执行取消操作，等待操作结束后，响应OK
		JobHistory history=context.getJobHistoryManager().findJobHistory(historyId);
		final String jobId=history.getJobId();
		SocketLog.info("receive master cancel request,rid="+req.getRid()+",jobId="+jobId);
		if(!context.getRunnings().containsKey(jobId)){
			return context.getThreadPool().submit(new Callable<Response>() {
				public Response call() throws Exception {
					return Response.newBuilder().setRid(req.getRid()).setOperate(Operate.Cancel).setStatus(Status.ERROR)
					.setErrorText("运行任务中查无此任务").build();
				}
			});
		}
		return context.getThreadPool().submit(new Callable<Response>() {
			public Response call() throws Exception {
				context.getClientWorker().cancelScheduleJob(jobId);
				Response resp=Response.newBuilder().setRid(req.getRid()).setOperate(Operate.Cancel).setStatus(Status.OK).build();
				return resp;
			}
		});
	}
	public Future<Response> cancelDebug(final WorkerContext context,final Request req,final String debugId){
		Future<Response> future=null;
		SocketLog.info("receive master cancel debug request,rid="+req.getRid()+",debugId="+debugId);
		if(!context.getDebugRunnings().containsKey(debugId)){
			future= context.getThreadPool().submit(new Callable<Response>() {
				public Response call() throws Exception {
					return Response.newBuilder().setRid(req.getRid()).setOperate(Operate.Cancel).setStatus(Status.ERROR)
					.setErrorText("运行任务中查无此任务").build();
				}
			});
			DebugHistory history=context.getDebugHistoryManager().findDebugHistory(debugId);
			history.setStatus(com.taobao.zeus.model.JobStatus.Status.FAILED);
			history.setEndTime(new Date());
			context.getDebugHistoryManager().updateDebugHistory(history);
		}else{
			SocketLog.info("send cancel debug response to worker,rid="+req.getRid()+",debugId="+debugId);
			future= context.getThreadPool().submit(new Callable<Response>() {
				public Response call() throws Exception {
					context.getClientWorker().cancelDebugJob(debugId);
					Response resp=Response.newBuilder().setRid(req.getRid()).setOperate(Operate.Cancel).setStatus(Status.OK).build();
					return resp;
				}
			});
		}
		return future;
	}
}
