package com.taobao.zeus.schedule;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.schedule.mvc.ScheduleInfoLog;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.store.WorkerManager;
import com.taobao.zeus.util.Environment;

/**
 * Zeus 调度系统
 * @author zhoufang
 *
 */
public class ZeusSchedule{

	private AtomicBoolean running=new AtomicBoolean(false);
	
	private MasterContext context;
	private ApplicationContext applicationContext;
	
	private WorkerManager workerManager;
	public ZeusSchedule(ApplicationContext applicationContext){
		this.applicationContext=applicationContext;
		workerManager = (WorkerManager) applicationContext.getBean("workerManager");
	}
	
	public void startup(int port){
		if(!getRunning().compareAndSet(false, true)){
			return;
		}
		List<String> masterGroup = workerManager.getDefaultMasterHost();
		if (masterGroup.contains(DistributeLocker.host)) {
			context=new MasterContext(applicationContext);
			context.init(port);
		}else {
			getRunning().compareAndSet(true, false);
			ScheduleInfoLog.info(DistributeLocker.host + " is not in master gourp");
		}
	}
	
	public void shutdown(){
		if(getRunning().compareAndSet(true, false)){
			context.destory();
		}
	}

	public AtomicBoolean getRunning() {
		return running;
	}
}
