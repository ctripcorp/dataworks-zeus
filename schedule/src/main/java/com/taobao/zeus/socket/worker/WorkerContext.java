package com.taobao.zeus.socket.worker;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.jobs.Job;
import com.taobao.zeus.store.DebugHistoryManager;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.JobHistoryManager;
import com.taobao.zeus.util.RunShell;

public class WorkerContext {
	private final static Logger log  = LoggerFactory.getLogger(WorkerContext.class);
	public static String host;
	private static Integer cpuCoreNum;
	private final static String findCpuCoreNumber="grep 'model name' /proc/cpuinfo | wc -l";
	static{
		try {
			host=InetAddress.getLocalHost().getHostAddress();
			if ("127.0.0.1".equals(host)) {
				log.error("host address error");
			}
			
		} catch (UnknownHostException e) {
			//ignore
		}
		String os=System.getProperties().getProperty("os.name");
		if(os!=null && (os.startsWith("win") || os.startsWith("Win") || os.startsWith("Mac"))){
			//ignore
		}
		else{
			try {
				RunShell runShell = new RunShell(findCpuCoreNumber);
				if(runShell.run() == 0){
					String result = runShell.getResult();
					if (result != null) {
						cpuCoreNum = Integer.valueOf(result);
					}
					
				}

			} catch (Exception e) {
				log.error("error",e);
			}
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
	public Integer getCpuCoreNum() {
		return cpuCoreNum;
	}
}
