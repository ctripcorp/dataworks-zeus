package com.taobao.zeus.socket.master;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.mvc.Dispatcher;
import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;
import com.taobao.zeus.store.DebugHistoryManager;
import com.taobao.zeus.store.FileManager;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.JobHistoryManager;
import com.taobao.zeus.store.ProfileManager;

public class MasterContext {
	private Map<Channel, MasterWorkerHolder> workers=new ConcurrentHashMap<Channel, MasterWorkerHolder>();
	private ApplicationContext applicationContext;
	private Master master;
	private Scheduler scheduler;
	private Dispatcher dispatcher;
	//调度任务 jobId
	private Queue<JobElement> queue=new ArrayBlockingQueue<JobElement>(10000);
	//调试任务  debugId
	private Queue<JobElement> debugQueue=new ArrayBlockingQueue<JobElement>(1000);
	//手动任务  historyId
	private Queue<JobElement> manualQueue=new ArrayBlockingQueue<JobElement>(1000);
	private MasterHandler handler;
	private MasterServer server;
	private ExecutorService threadPool=Executors.newCachedThreadPool();
	private ScheduledExecutorService schedulePool=Executors.newScheduledThreadPool(5);
	
	public MasterContext(ApplicationContext applicationContext){
		this.applicationContext=applicationContext;
	}
	public void init(int port){
		try {
			scheduler=new StdSchedulerFactory().getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			ScheduleInfoLog.error("schedule start fail", e);
		}
		dispatcher=new Dispatcher();
		handler=new MasterHandler(this);
		server=new MasterServer(handler);
		server.start(port);
		master=new Master(this);
	}
	public void destory(){
		threadPool.shutdown();
		schedulePool.shutdown();
		if(server!=null){
			server.shutdown();
		}
		if(scheduler!=null){
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Map<Channel, MasterWorkerHolder> getWorkers() {
		return workers;
	}
	public void setWorkers(Map<Channel, MasterWorkerHolder> workers) {
		this.workers = workers;
	}
	public Scheduler getScheduler() {
		return scheduler;
	}
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	public Dispatcher getDispatcher() {
		return dispatcher;
	}
	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	public JobHistoryManager getJobHistoryManager() {
		return (JobHistoryManager) applicationContext.getBean("jobHistoryManager");
	}
	public DebugHistoryManager getDebugHistoryManager(){
		return (DebugHistoryManager)applicationContext.getBean("debugHistoryManager");
	}
	public FileManager getFileManager(){
		return (FileManager) applicationContext.getBean("fileManager");
	}
	public ProfileManager getProfileManager(){
		return (ProfileManager) applicationContext.getBean("profileManager");
	}
	public Queue<JobElement> getQueue() {
		return queue;
	}
	public void setQueue(Queue<JobElement> queue) {
		this.queue = queue;
	}
	public GroupManager getGroupManager() {
		return (GroupManager) applicationContext.getBean("groupManager");
	}
	public MasterHandler getHandler() {
		return handler;
	}
	public void setHandler(MasterHandler handler) {
		this.handler = handler;
	}
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public MasterServer getServer() {
		return server;
	}
	public void setServer(MasterServer server) {
		this.server = server;
	}
	public ExecutorService getThreadPool() {
		return threadPool;
	}
	public Master getMaster() {
		return master;
	}
	public void setMaster(Master master) {
		this.master = master;
	}
	public ScheduledExecutorService getSchedulePool() {
		return schedulePool;
	}
	public Queue<JobElement> getDebugQueue() {
		return debugQueue;
	}
	public void setDebugQueue(Queue<JobElement> debugQueue) {
		this.debugQueue = debugQueue;
	}
	public Queue<JobElement> getManualQueue() {
		return manualQueue;
	}
}
