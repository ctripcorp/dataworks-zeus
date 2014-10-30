package com.taobao.zeus.socket.worker;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.jobs.Job;
import com.taobao.zeus.store.DebugHistoryManager;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.JobHistoryManager;

public class WorkerContext {
	public static String host;
	static{
		try {
			host=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			//ignore
		}
	}
	private String serverHost;
	private Channel serverChannel;
	private Map<String, Job> runnings=new ConcurrentHashMap<String, Job>();
	private Map<String, Job> manualRunnings=new ConcurrentHashMap<String, Job>();
	private Map<String,Job> debugRunnings=new ConcurrentHashMap<String, Job>();
	private WorkerHandler handler;
	private ClientWorker clientWorker;
	private ExecutorService threadPool=Executors.newCachedThreadPool();
	private ApplicationContext applicationContext;
	public String getServerHost() {
		return serverHost;
	}
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}
	public Channel getServerChannel() {
		return serverChannel;
	}
	public void setServerChannel(Channel serverChannel) {
		this.serverChannel = serverChannel;
	}
	public Map<String, Job> getRunnings() {
		return runnings;
	}
	public void setRunnings(Map<String, Job> runnings) {
		this.runnings = runnings;
	}
	public WorkerHandler getHandler() {
		return handler;
	}
	public void setHandler(WorkerHandler handler) {
		this.handler = handler;
	}
	public ExecutorService getThreadPool() {
		return threadPool;
	}
	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}
	public ClientWorker getClientWorker() {
		return clientWorker;
	}
	public void setClientWorker(ClientWorker clientWorker) {
		this.clientWorker = clientWorker;
	}
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	public JobHistoryManager getJobHistoryManager() {
		return (JobHistoryManager) applicationContext.getBean("jobHistoryManager");
	}
	public DebugHistoryManager getDebugHistoryManager(){
		return (DebugHistoryManager)applicationContext.getBean("debugHistoryManager");
	}
	public GroupManager getGroupManager() {
		return (GroupManager) applicationContext.getBean("groupManager");
	}
	public Map<String, Job> getDebugRunnings() {
		return debugRunnings;
	}
	public Map<String, Job> getManualRunnings() {
		return manualRunnings;
	}
	@Override
	public String toString() {
		return "WorkerContext [serverHost=" + serverHost + ", serverChannel="
				+ serverChannel + ", runnings=" + runnings
				+ ", manualRunnings=" + manualRunnings + ", debugRunnings="
				+ debugRunnings + ", handler=" + handler + ", clientWorker="
				+ clientWorker + ", threadPool=" + threadPool
				+ ", applicationContext=" + applicationContext + "]";
	}
}
