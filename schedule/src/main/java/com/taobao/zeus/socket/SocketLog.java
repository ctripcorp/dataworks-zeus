package com.taobao.zeus.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketLog {

	private static Logger log=LoggerFactory.getLogger(SocketLog.class);
	
	public static void info(String msg){
		log.error(msg);
	}
	public static void error(String msg,Throwable t){
		log.error(msg, t);
	}
}
