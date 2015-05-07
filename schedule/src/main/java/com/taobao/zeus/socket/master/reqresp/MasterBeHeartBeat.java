package com.taobao.zeus.socket.master.reqresp;

import java.util.Date;

import org.jboss.netty.channel.Channel;

import com.google.protobuf.InvalidProtocolBufferException;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.socket.master.MasterWorkerHolder;
import com.taobao.zeus.socket.master.MasterWorkerHolder.HeartBeatInfo;
import com.taobao.zeus.socket.protocol.Protocol.HeartBeatMessage;
import com.taobao.zeus.socket.protocol.Protocol.Request;

public class MasterBeHeartBeat {
	public void beHeartBeat(MasterContext context,Channel channel,Request request) {
		MasterWorkerHolder worker=context.getWorkers().get(channel);
		HeartBeatInfo newbeat=worker.new HeartBeatInfo();
		HeartBeatMessage hbm;
		try {
			hbm = HeartBeatMessage.newBuilder().mergeFrom(request.getBody()).build();
			newbeat.memRate=hbm.getMemRate();
			newbeat.runnings=hbm.getRunningsList();
			newbeat.debugRunnings=hbm.getDebugRunningsList();
			newbeat.manualRunnings=hbm.getManualRunningsList();
			newbeat.timestamp=new Date(hbm.getTimestamp());
			newbeat.host=hbm.getHost();
			newbeat.cpuLoadPerCore=hbm.getCpuLoadPerCore();
			if(worker.heart==null || newbeat.timestamp.getTime()>worker.heart.timestamp.getTime()){
				worker.heart=newbeat;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
}
