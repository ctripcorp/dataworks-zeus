package com.taobao.zeus.schedule;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationContext;

import com.taobao.zeus.socket.master.MasterContext;

/**
 * Zeus 调度系统
 * @author zhoufang
 *
 */
public class ZeusSchedule{

	private AtomicBoolean running=new AtomicBoolean(false);
	
	private MasterContext context;
	private ApplicationContext applicationContext;
	public ZeusSchedule(ApplicationContext applicationContext){
		this.applicationContext=applicationContext;
	}
	
	public void startup(int port){
		if(!running.compareAndSet(false, true)){
			return;
		}
		context=new MasterContext(applicationContext);
		context.init(port);
	}
	
	public void shutdown(){
		if(running.compareAndSet(true, false)){
			context.destory();
		}
	}
}
