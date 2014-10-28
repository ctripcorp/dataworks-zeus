package com.taobao.zeus.socket.master;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;
import com.taobao.zeus.socket.SocketLog;
import com.taobao.zeus.socket.master.reqresp.MasterBeHeartBeat;
import com.taobao.zeus.socket.master.reqresp.MasterBeUpdate;
import com.taobao.zeus.socket.master.reqresp.MasterBeWebCancel;
import com.taobao.zeus.socket.master.reqresp.MasterBeWebDebug;
import com.taobao.zeus.socket.master.reqresp.MasterBeWebExecute;
import com.taobao.zeus.socket.protocol.Protocol.Operate;
import com.taobao.zeus.socket.protocol.Protocol.Request;
import com.taobao.zeus.socket.protocol.Protocol.Response;
import com.taobao.zeus.socket.protocol.Protocol.SocketMessage;
import com.taobao.zeus.socket.protocol.Protocol.WebOperate;
import com.taobao.zeus.socket.protocol.Protocol.WebRequest;
import com.taobao.zeus.socket.protocol.Protocol.WebResponse;
import com.taobao.zeus.socket.protocol.Protocol.SocketMessage.Kind;

public class MasterHandler extends SimpleChannelUpstreamHandler{

	private CompletionService<ChannelResponse> completionService=new ExecutorCompletionService<ChannelResponse>(Executors.newCachedThreadPool());
	
	private class ChannelResponse{
		Channel channel;
		WebResponse resp;
		public ChannelResponse(Channel channel,WebResponse resp){
			this.channel=channel;
			this.resp=resp;
		}
	}
	private MasterContext context;
	public MasterHandler(MasterContext context){
		this.context=context;
		new Thread(){
			public void run() {
				while(true){
					try {
						Future<ChannelResponse> f=completionService.take();
						ChannelResponse resp=f.get();
						resp.channel.write(wapper(resp.resp));
					} catch (Exception e) {
						ScheduleInfoLog.error("master handler,future take", e);
					}
				}
			};
		}.start();
	}
	private SocketMessage wapper(WebResponse resp){
		return SocketMessage.newBuilder().setKind(Kind.WEB_RESPONSE).setBody(resp.toByteString()).build();
	}
	private MasterBeHeartBeat beHeartBeat=new MasterBeHeartBeat();
	private MasterBeUpdate beUpdate=new MasterBeUpdate();
	private MasterBeWebCancel beWebCancel=new MasterBeWebCancel();
	private MasterBeWebExecute beWebExecute=new MasterBeWebExecute();
	private MasterBeWebDebug beDebug=new MasterBeWebDebug();
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		final Channel channel=ctx.getChannel();
		SocketMessage sm=(SocketMessage) e.getMessage();
		if(sm.getKind()==Kind.REQUEST){
			final Request request=Request.newBuilder().mergeFrom(sm.getBody()).build();
			if(request.getOperate()==Operate.HeartBeat){
				beHeartBeat.beHeartBeat(context, channel, request);
			}
		}else if(sm.getKind()==Kind.WEB_REUQEST){
			final WebRequest request=WebRequest.newBuilder().mergeFrom(sm.getBody()).build();
			 if(request.getOperate()==WebOperate.ExecuteJob){
				completionService.submit(new Callable<ChannelResponse>() {
					public ChannelResponse call() throws Exception {
						return new ChannelResponse(channel,beWebExecute.beWebExecute(context,request));
					}
				});
			}else if(request.getOperate()==WebOperate.CancelJob){
				completionService.submit(new Callable<ChannelResponse>() {
					public ChannelResponse call() throws Exception {
						return new ChannelResponse(channel,beWebCancel.beWebCancel(context,request));
					}
				});
			}else if(request.getOperate()==WebOperate.UpdateJob){
				completionService.submit(new Callable<ChannelResponse>() {
					public ChannelResponse call() throws Exception {
						return  new ChannelResponse(channel,beUpdate.beWebUpdate(context,request));
					}
				});
			}else if(request.getOperate()==WebOperate.ExecuteDebug){
				completionService.submit(new Callable<ChannelResponse>() {
					public ChannelResponse call() throws Exception {
						return new ChannelResponse(channel, beDebug.beWebExecute(context, request));
					}
				});
			}
		}else if(sm.getKind()==Kind.RESPONSE){
			for(ResponseListener lis:new ArrayList<ResponseListener>(listeners)){
				lis.onResponse(Response.newBuilder().mergeFrom(sm.getBody()).build());
			}
		}else if(sm.getKind()==Kind.WEB_RESPONSE){
			for(ResponseListener lis:new ArrayList<ResponseListener>(listeners)){
				lis.onWebResponse(WebResponse.newBuilder().mergeFrom(sm.getBody()).build());
			}
		}
		
		super.messageReceived(ctx, e);
	}
	@Override
	public void channelConnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		context.getWorkers().put(ctx.getChannel(), new MasterWorkerHolder(ctx.getChannel()));
		Channel channel=ctx.getChannel();
		SocketAddress addr=channel.getRemoteAddress();
		SocketLog.info("worker connected , :"+addr.toString());
		super.channelConnected(ctx, e);
	}
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		SocketLog.info("worker disconnect :"+ctx.getChannel().getRemoteAddress().toString());
		context.getMaster().workerDisconnectProcess(ctx.getChannel());
		super.channelDisconnected(ctx, e);
	}
	private List<ResponseListener> listeners=new CopyOnWriteArrayList<ResponseListener>();
	public void addListener(ResponseListener listener){
		listeners.add(listener);
	}
	public void removeListener(ResponseListener listener){
		listeners.remove(listener);
	}
	public static interface ResponseListener{
		public void onResponse(Response resp);
		public void onWebResponse(WebResponse resp);
	}
}
