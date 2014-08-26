package com.taobao.zeus.util;

import java.io.File;


/**
 * 环境类
 * 用于判断当前是哪个环境
 * 在spring中进行设置
 * @author zhoufang
 *
 */
public class Environment {

	private static String env;
	
	/**
	 * 执行job需要下载资源文件时用到的路径
	 */
	private static String downloadPath;
	private static String scheduleGroup;
	
	public Environment(String env,String scheduleGroup,String downloadPath){
		Environment.env=env;
		Environment.scheduleGroup=scheduleGroup;
		Environment.downloadPath=downloadPath;
		File file=new File(downloadPath);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
	public static Boolean isOnline(){
		return "online".equalsIgnoreCase(env);
	}
	
	public static Boolean isDaily(){
		return "daily".equalsIgnoreCase(env);
	}
	public static Boolean isPrePub(){
		return "prepub".equalsIgnoreCase(env);
	}

	public static String getScheduleGroup() {
		return scheduleGroup;
	}

	public static String getDownloadPath() {
		return downloadPath;
	}
}
