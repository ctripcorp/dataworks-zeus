package com.taobao.zeus.socket.master;


import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;
import com.taobao.zeus.socket.protocol.Protocol;


public class MasterServer{
	// Server服务启动器
	private ServerBootstrap bootstrap;
	private ChannelPipelineFactory pipelineFactory;
	
	public MasterServer(final ChannelHandler handler){
		NioServerSocketChannelFactory channelFactory=
			new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		bootstrap=new ServerBootstrap(channelFactory);
		pipelineFactory=new ChannelPipelineFactory(){
			private final ProtobufVarint32LengthFieldPrepender frameEncoder = new ProtobufVarint32LengthFieldPrepender();
			private final ProtobufEncoder protobufEncoder = new ProtobufEncoder();
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline p = pipeline();
				p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
				p.addLast("protobufDecoder",new ProtobufDecoder(Protocol.SocketMessage.getDefaultInstance()));
				p.addLast("frameEncoder", frameEncoder);
				p.addLast("protobufEncoder", protobufEncoder);
				p.addLast("handler", handler);
				return p;
			}
			
		};
		try {
			bootstrap.setPipeline(pipelineFactory.getPipeline());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void start(int port){
		bootstrap.bind(new InetSocketAddress(port));
		ScheduleInfoLog.info("netty server start");
	}
	
	public synchronized void shutdown(){
		bootstrap.releaseExternalResources();
		ScheduleInfoLog.info("netty server shutdown");
	}
}
