package com.taobao.zeus.socket.master.reqresp;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.socket.SocketLog;
import com.taobao.zeus.socket.master.AtomicIncrease;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.socket.master.MasterWorkerHolder;
import com.taobao.zeus.socket.master.MasterHandler.ResponseListener;
import com.taobao.zeus.socket.protocol.Protocol.DebugMessage;
import com.taobao.zeus.socket.protocol.Protocol.ExecuteKind;
import com.taobao.zeus.socket.protocol.Protocol.ExecuteMessage;
import com.taobao.zeus.socket.protocol.Protocol.ManualMessage;
import com.taobao.zeus.socket.protocol.Protocol.Operate;
import com.taobao.zeus.socket.protocol.Protocol.Request;
import com.taobao.zeus.socket.protocol.Protocol.Response;
import com.taobao.zeus.socket.protocol.Protocol.SocketMessage;
import com.taobao.zeus.socket.protocol.Protocol.WebResponse;
import com.taobao.zeus.socket.protocol.Protocol.SocketMessage.Kind;

public class MasterExecuteJob {
	/**
	 * master向worker发送执行job的指令
	 * @param context
	 * @param jobId
	 * @return
	 */
	public Future<Response> executeJob(final MasterContext context,final MasterWorkerHolder holder,ExecuteKind ek,final String id){
		if(ek==ExecuteKind.ManualKind){
			return processManual(context, holder, id);
		}else if(ek==ExecuteKind.ScheduleKind){
			return processSchedule(context, holder, id);
		}else if(ek==ExecuteKind.DebugKind){
			return processDebug(context, holder, id);
		}
		return null;
	}
	
	private Future<Response> processSchedule(final MasterContext context,final MasterWorkerHolder holder,final String id){
		// 向channel 发送执行job命令
		// 等待worker响应
		// 响应OK 则添加监听器，继续等待任务完成的消息
		// 响应失败，返回失败退出码
		JobHistory history=context.getJobHistoryManager().findJobHistory(id);
		final String jobId=history.getJobId();
		holder.getRunnings().put(jobId,false);
		ExecuteMessage em=ExecuteMessage.newBuilder().setJobId(jobId).build();
		final Request req=Request.newBuilder().setRid(AtomicIncrease.getAndIncrement()).setOperate(Operate.Schedule)
			.setBody(em.toByteString()).build();
		SocketMessage sm=SocketMessage.newBuilder().setKind(Kind.REQUEST).setBody(req.toByteString()).build();
		Future<Response> f=context.getThreadPool().submit(new Callable<Response>() {
			private Response response;
			public Response call() throws Exception {
				final CountDownLatch latch=new CountDownLatch(1);
				context.getHandler().addListener(new ResponseListener() {
					public void onWebResponse(WebResponse resp) {}
					public void onResponse(Response resp) {
						if(resp.getRid()==req.getRid()){
							context.getHandler().removeListener(this);
							response=resp;
							latch.countDown();
						}
					}
				});
				try {
					latch.await();
				} finally{
					holder.getRunnings().remove(jobId);
				}
				return response;
			}
		});
		holder.getChannel().write(sm);
		SocketLog.info("master send execute command to worker,rid="+req.getRid()+",jobId="+jobId);
		return f;
	}
	private Future<Response> processManual(final MasterContext context,final MasterWorkerHolder holder,final String historyId){
		// 向channel 发送执行job命令
		// 等待worker响应
		// 响应OK 则添加监听器，继续等待任务完成的消息
		// 响应失败，返回失败退出码
		holder.getManualRunnings().put(historyId,false);
		ManualMessage mm=ManualMessage.newBuilder().setHistoryId(historyId).build();
		final Request req=Request.newBuilder().setRid(AtomicIncrease.getAndIncrement()).setOperate(Operate.Manual)
			.setBody(mm.toByteString()).build();
		SocketMessage sm=SocketMessage.newBuilder().setKind(Kind.REQUEST).setBody(req.toByteString()).build();
		Future<Response> f=context.getThreadPool().submit(new Callable<Response>() {
			private Response response;
			public Response call() throws Exception {
				final CountDownLatch latch=new CountDownLatch(1);
				context.getHandler().addListener(new ResponseListener() {
					public void onWebResponse(WebResponse resp) {}
					public void onResponse(Response resp) {
						if(resp.getRid()==req.getRid()){
							context.getHandler().removeListener(this);
							response=resp;
							latch.countDown();
						}
					}
				});
				try {
					latch.await();
				} finally{
					holder.getManualRunnings().remove(historyId);
				}
				return response;
			}
		});
		holder.getChannel().write(sm);
		SocketLog.info("master send manual command to worker,rid="+req.getRid()+",historyId="+historyId);
		return f;
	}
	private Future<Response> processDebug(final MasterContext context,final MasterWorkerHolder holder,final String id){
		// 向channel 发送执行job命令
		// 等待worker响应
		// 响应OK 则添加监听器，继续等待任务完成的消息
		// 响应失败，返回失败退出码
		holder.getDebugRunnings().put(id,false);
		DebugMessage dm=DebugMessage.newBuilder().setDebugId(id).build();
		final Request req=Request.newBuilder().setRid(AtomicIncrease.getAndIncrement()).setOperate(Operate.Debug)
			.setBody(dm.toByteString()).build();
		SocketMessage sm=SocketMessage.newBuilder().setKind(Kind.REQUEST).setBody(req.toByteString()).build();
		Future<Response> f=context.getThreadPool().submit(new Callable<Response>() {
			private Response response;
			public Response call() throws Exception {
				final CountDownLatch latch=new CountDownLatch(1);
				context.getHandler().addListener(new ResponseListener() {
					public void onWebResponse(WebResponse resp) {}
					public void onResponse(Response resp) {
						if(resp.getRid()==req.getRid()){
							context.getHandler().removeListener(this);
							response=resp;
							latch.countDown();
						}
					}
				});
				try {
					latch.await();
				} finally{
					holder.getDebugRunnings().remove(id);
				}
				return response;
			}
		});
		holder.getChannel().write(sm);
		SocketLog.info("master send debug command to worker,rid="+req.getRid()+",debugId="+id);
		return f;
	}
}
