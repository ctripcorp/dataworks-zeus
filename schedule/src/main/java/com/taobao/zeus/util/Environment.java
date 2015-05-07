package com.taobao.zeus.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 环境类
 * 用于判断当前是哪个环境
 * 在spring中进行设置
 * @author zhoufang
 *
 */
public class Environment {

	private static Logger log = LoggerFactory.getLogger(Environment.class);
	private static String env;
	/**
	 * 执行job需要下载资源文件时用到的路径
	 */
	private static String downloadPath;
	private static String scheduleGroup;
	
	private static String hadoopHome;
	private static String hadoopConfDir;
	private static String hiveHome;
	private static String hiveConfDir;
	private static String host;
	private static String port;
	private static String username;
	private static String password;
	private static String sendFrom;
	private static String notifyUrl;
	private static String accessToken;
	private static String excludeFile;
	private static String defaultWorkerGroupId;
	private static String defaultMasterGroupId;
	


	private static Float maxMemRate;//默认值
	private static Float maxCpuLoadPerCore;//默认值
	

	public Environment(String env,String scheduleGroup,String downloadPath,String hadoopHome,String hadoopConfDir,String hiveHome,String hiveConfDir,
			String host,String port,String username,String password,String sendFrom,String notifyUrl,String accessToken,String excludeFile, String defaultWorkerGroupId, String defaultMasterGroupId
			,String maxMemRate, String maxCpuLoadPerCore){
		Environment.env=env.trim();
		Environment.scheduleGroup=scheduleGroup.trim();
		Environment.downloadPath=downloadPath.trim();
		File file=new File(downloadPath);
		if(!file.exists()){
			file.mkdirs();
		}
		Environment.hadoopHome=hadoopHome.trim();
		Environment.hadoopConfDir=hadoopConfDir.trim();
		Environment.hiveHome=hiveHome.trim();
		Environment.hiveConfDir=hiveConfDir.trim();
		Environment.host=host.trim();
		Environment.port=port.trim();
		Environment.username=username.trim();
		Environment.password=password.trim();
		Environment.sendFrom=sendFrom.trim();
		Environment.notifyUrl=notifyUrl.trim();
		Environment.accessToken=accessToken.trim();
		Environment.excludeFile=excludeFile.trim();
		Environment.defaultWorkerGroupId=defaultWorkerGroupId.trim();
		Environment.defaultMasterGroupId=defaultMasterGroupId.trim();
		try {
			Environment.maxMemRate=0.8f;//默认值
			Environment.maxMemRate=Float.valueOf(maxMemRate);
		} catch (Exception e) {
			Environment.maxMemRate=0.8f;//默认值
			log.error("MaxMemRate initialize error, using default value", e);
		}
		try {
			Environment.maxCpuLoadPerCore=3.0f;//默认值
			Environment.maxCpuLoadPerCore=Float.valueOf(maxCpuLoadPerCore);
		} catch (Exception e) {
			Environment.maxCpuLoadPerCore=3.0f;//默认值
			log.error("MaxCpuLoadPerCore initialize error, using default value", e);
		}
		log.info("the env is " + env.trim());
		log.info("the downloadPath is " + downloadPath.trim());
		log.info("the hadoopHome is " + hadoopHome.trim());
		log.info("the hadoopConfDir is " + hadoopConfDir.trim());
		log.info("the hiveHome is " + hiveHome.trim());
		log.info("the hiveConfDir is " + hiveConfDir.trim());
		log.info("the host is " + host.trim());
		log.info("the port is " + port.trim());
		log.info("the excludeFile is " + excludeFile.trim());
		log.info("the defaultWorkerGroupId is " + defaultWorkerGroupId.trim());
		log.info("the defaultMasterGroupId is " + defaultMasterGroupId.trim());
		log.info("the maxMemRate is " + maxMemRate.toString());
		log.info("the maxCpuLoadPerCore is " + maxCpuLoadPerCore.toString());
	}
	
	public static String getNotifyUrl() {
		return notifyUrl;
	}

	public static String getAccessToken() {
		return accessToken;
	}

	public static String getExcludeFile() {
		return excludeFile;
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
	
	public static String getHadoopHome() {
		return hadoopHome;
	}
	
	public static String getHadoopConfDir() {
		return hadoopConfDir;
	}
	
	public static String getHiveHome() {
		return hiveHome;
	}
	
	public static String getHiveConfDir() {
		return hiveConfDir;
	}

	public static String getHost() {
		return host;
	}

	public static String getPort() {
		return port;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getSendFrom() {
		return sendFrom;
	}

	public static String getDefaultWorkerGroupId() {
		return defaultWorkerGroupId;
	}

	public static String getDefaultMasterGroupId() {
		return defaultMasterGroupId;
	}
	public static Float getMaxMemRate() {
		return maxMemRate;
	}

	public static void setMaxMemRate(Float maxMemRate) {
		Environment.maxMemRate = maxMemRate;
	}

	public static Float getMaxCpuLoadPerCore() {
		return maxCpuLoadPerCore;
	}

	public static void setMaxCpuLoadPerCore(Float maxCpuLoadPerCore) {
		Environment.maxCpuLoadPerCore = maxCpuLoadPerCore;
	}
}
