package com.taobao.zeus.schedule;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.socket.master.MasterContext;

/**
 * Zeus 调度系统
 * @author zhoufang
 *
 */
public class ZeusSchedule{
	
	private static Logger log = LoggerFactory.getLogger(ZeusSchedule.class);
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
		log.info("begin to initialize master context");
		context=new MasterContext(applicationContext);
		log.info("begin to init");
		context.init(port);
	}
	
	public void shutdown(){
		if(running.compareAndSet(true, false)){
			context.destory();
		}
	}
}
