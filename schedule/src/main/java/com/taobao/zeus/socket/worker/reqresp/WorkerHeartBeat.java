package com.taobao.zeus.socket.worker.reqresp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import org.jboss.netty.channel.ChannelFuture;

import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.sub.tool.CpuLoadPerCoreJob;
import com.taobao.zeus.jobs.sub.tool.MemUseRateJob;
import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;
import com.taobao.zeus.socket.master.AtomicIncrease;
import com.taobao.zeus.socket.protocol.Protocol.HeartBeatMessage;
import com.taobao.zeus.socket.protocol.Protocol.Operate;
import com.taobao.zeus.socket.protocol.Protocol.Request;
import com.taobao.zeus.socket.protocol.Protocol.SocketMessage;
import com.taobao.zeus.socket.protocol.Protocol.SocketMessage.Kind;
import com.taobao.zeus.socket.worker.WorkerContext;
import com.taobao.zeus.util.Environment;

public class WorkerHeartBeat {
	public static String host = UUID.randomUUID().toString();
	static {
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// ignore
		}
	}

	public ChannelFuture execute(WorkerContext context) {
		JobContext jobContext = JobContext.getTempJobContext(JobContext.SYSTEM_RUN);
		MemUseRateJob job = new MemUseRateJob(jobContext, 1);
		CpuLoadPerCoreJob loadJob = new CpuLoadPerCoreJob(context, jobContext);
		runJob(jobContext, job);
		runJob(jobContext, loadJob);
		HeartBeatMessage hbm = HeartBeatMessage.newBuilder()
				.setCpuLoadPerCore((Float)jobContext.getData("cpuLoadPerCore"))
				.setMemRate(((Double) jobContext.getData("mem")).floatValue())
				.addAllDebugRunnings(context.getDebugRunnings().keySet())
				.addAllManualRunnings(context.getManualRunnings().keySet())
				.addAllRunnings(context.getRunnings().keySet())
				.setTimestamp(new Date().getTime()).setHost(host).build();
		Request req = Request.newBuilder()
				.setRid(AtomicIncrease.getAndIncrement())
				.setOperate(Operate.HeartBeat).setBody(hbm.toByteString())
				.build();

		SocketMessage sm = SocketMessage.newBuilder().setKind(Kind.REQUEST)
				.setBody(req.toByteString()).build();
		return context.getServerChannel().write(sm);
	}

	private void runJob(JobContext jobContext, MemUseRateJob job) {
		try {
			int exitCode = -1;
			int count = 0;
			while (count < 3 && exitCode != 0) {
				count++;
				exitCode = job.run();
			}
			if (exitCode != 0) {
				ScheduleInfoLog.error("HeartBeat Shell Error", new Exception(
						jobContext.getJobHistory().getLog().getContent()));
				// 防止后面NPE
				jobContext.putData("mem", 1.0);
			}
		} catch (Exception e) {
			ScheduleInfoLog.error("memratejob", e);
		}
	}
	
	private void runJob(JobContext jobContext, CpuLoadPerCoreJob job) {
		try {
			int exitCode = -1;
			int count = 0;
			while (count < 3 && exitCode != 0) {
				count++;
				exitCode = job.run();
			}
			if (exitCode != 0) {
				ScheduleInfoLog.error("HeartBeat Shell Error", new Exception(" error occurs during get cpu load "));
				// 防止后面NPE
				jobContext.putData("cpuLoadPerCore",Environment.getMaxCpuLoadPerCore());
			}
		} catch (Exception e) {
			ScheduleInfoLog.error("cpuLoadPerCore", e);
		}
	}
}
