package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.sencha.gxt.widget.core.client.info.DefaultInfoConfig;
import com.sencha.gxt.widget.core.client.info.InfoConfig;

@SuppressWarnings("serial")
public abstract class ProcesserType extends HashMap<String, String>{
	
	public abstract String getJsonObject();
	public abstract InfoConfig getInfoConfig();
	
	public String getName(){
		return get("name");
	}
	public String getId(){
		return get("id");
	}
	@Override
	public boolean equals(final Object obj) {
		if(obj instanceof ProcesserType){
			return ((ProcesserType)obj).getId().equals(getId());
		}
		return super.equals(obj);
	}
	
	
	
	public static ProcesserType parse(final String text){
		ProcesserType result=null;
		JSONObject o=(JSONObject) JSONParser.parseStrict(text);
		String id=o.get("id").isString().stringValue();
		if("download".equalsIgnoreCase(id)){
			result= new ProcesserType.DownloadP();
		}else if("zookeeper".equalsIgnoreCase(id)){
			ZooKeeperP zk=new ZooKeeperP();
			JSONObject config=(JSONObject)o.get("config");
			zk.setUseDefault(config.get("useDefault").isBoolean().booleanValue());
			zk.setHost(config.get("host").isString().stringValue());
			zk.setPath(config.get("path").isString().stringValue());
			result=zk;
		}else if("mail".equalsIgnoreCase(id)){
			MailP mail=new MailP();
			JSONObject config=(JSONObject) o.get("config");
			mail.setSubject(config.get("subject").isString().stringValue());
			mail.setTemplate(config.get("template").isString().stringValue());
			result=mail;
		}else if("wangwang".equalsIgnoreCase(id)){
			//TODO
		}else if("meta".equalsIgnoreCase(id)){
			result=new MetaP();
		}else if(HiveP.ID.equals(id)){
			HiveP p = new HiveP();
			JSONObject config=(JSONObject)o.get("config");
			if(config.get("outputTables")!=null) {
				p.setOutputTables(config.get("outputTables").isString().stringValue());
			}
			if(config.get("driftPercent")!=null) {
				p.setDriftPercent(config.get("driftPercent").isString().stringValue());
			}
			if(config.get("keepDays")!=null) {
				p.setKeepDays(config.get("keepDays").isString().stringValue());
			}
			if(config.get("syncTables")!=null) {
				p.setSyncTables(config.get("syncTables").isString().stringValue());
			}
			result = p;
		}else if("JobProcesser".equalsIgnoreCase(id)){
			JobP jobP=new JobP();
			JSONObject config=(JSONObject)o.get("config");
			jobP.setJobId(config.get("jobId").isString().stringValue());
			JSONObject kvConfig=(JSONObject)o.get("kvConfig");
			Map<String, String> map=new HashMap<String, String>();
			if(kvConfig!=null){
				for(String key:kvConfig.keySet()){
					map.put(key, kvConfig.get(key).isString().stringValue());
				}
			}
			jobP.setKvConfig(map);
			result=jobP;
		}
		return result;
	}
	
	public static class HiveP extends ProcesserType{
		public static final String ID="hive";
		private String outputTables;
		private String syncTables;
		private String keepDays;
		private String driftPercent;
		
		public HiveP(){
			put("id",HiveP.ID);
			put("name", "Hive相关处理器");
		}
		@Override
		public String getJsonObject() {
			JSONObject config=new JSONObject();
			if(outputTables!=null) {
				config.put("outputTables", new JSONString(outputTables));
			}
			if(syncTables!=null) {
				config.put("syncTables", new JSONString(syncTables));
			}
			if(keepDays!=null) {
				config.put("keepDays", new JSONString(keepDays.toString()));
			}
			if(driftPercent!=null) {
				config.put("driftPercent", new JSONString(driftPercent.toString()));
			}
			JSONObject o=new JSONObject();
			o.put("id", new JSONString(HiveP.ID));
			o.put("config", config);
			return o.toString();
		}

		@Override
		public InfoConfig getInfoConfig() {
			// TODO getInfoConfig
			return null;
		}
		public String getOutputTables() {
			return outputTables;
		}
		public void setOutputTables(String outputTables) {
			this.outputTables = outputTables;
		}
		public String getSyncTables() {
			return syncTables;
		}
		public void setSyncTables(String syncTables) {
			this.syncTables = syncTables;
		}
		public String getKeepDays() {
			return keepDays;
		}
		public void setKeepDays(String keepDays) {
			this.keepDays = keepDays;
		}
		public String getDriftPercent() {
			return driftPercent;
		}
		public void setDriftPercent(String driftPercent) {
			this.driftPercent = driftPercent;
		}
		
	}
	
	public static class DownloadP extends ProcesserType{
		public static final String ID="download";
		public DownloadP(){
			put("id",DownloadP.ID);
			put("name", "资源下载");
		}
		@Override
		public String getJsonObject() {
			JSONObject o=new JSONObject();
			o.put("id", new JSONString("download"));
			return o.toString();
		}
		@Override
		public InfoConfig getInfoConfig() {
			InfoConfig config=new DefaultInfoConfig(getName(), "Job任务在执行时将会进行下载资源文件操作");
			return config;
		}
	}
	public static class ZooKeeperP extends ProcesserType{
		public static final String ID="zookeeper";
		private Boolean useDefault=true;
		private String host;
		private String path;
		public ZooKeeperP(){
			put("id",ZooKeeperP.ID);
			put("name", "ZK通知");
		}
		@Override
		public String getJsonObject() {
			JSONObject config=new JSONObject();
			config.put("useDefault", new JSONString(useDefault.toString()));
			host=host==null?"":host;
			config.put("host",new JSONString(host));
			path=path==null?"":path;
			config.put("path", new JSONString(path));
			JSONObject o=new JSONObject();
			o.put("id", new JSONString("zookeeper"));
			o.put("config", config);
			return o.toString();
		}
		@Override
		public InfoConfig getInfoConfig() {
			StringBuffer sb=new StringBuffer();
			if(getUseDefault()){
				sb.append("使用Zeus默认ZK地址");
			}else{
				sb.append("host:"+getHost()+"<br/>");
				sb.append("path:"+getPath()+"<br/>");
			}
			InfoConfig info=new DefaultInfoConfig(getName(),sb.toString());
			info.setWidth(300);
			return info;
		}
		public Boolean getUseDefault() {
			return useDefault;
		}
		public void setUseDefault(final Boolean useDefault) {
			this.useDefault = useDefault;
		}
		public String getHost() {
			return host;
		}
		public void setHost(final String host) {
			this.host = host;
		}
		public String getPath() {
			return path;
		}
		public void setPath(final String path) {
			this.path = path;
		}
	}
	
	public static class MailP extends ProcesserType{
		public static final String ID="mail";
		public MailP(){
			put("id",MailP.ID);
			put("name","邮件通知");
		}
		public String getTemplate(){
			return get("template");
		}
		public void setTemplate(final String template){
			put("template",template);
		}
		public String getSubject(){
			return get("subject");
		}
		public void setSubject(final String subject){
			put("subject",subject);
		}
		@Override
		public String getJsonObject() {
			JSONObject config=new JSONObject();
			config.put("subject", new JSONString(getSubject()));
			config.put("template", new JSONString(getTemplate()));
			JSONObject o=new JSONObject();
			o.put("id", new JSONString("mail"));
			o.put("config", config);
			return o.toString();
		}
		@Override
		public InfoConfig getInfoConfig() {
			return new DefaultInfoConfig(getSubject(), getTemplate());
		}
		
	}
	
	public static class MetaP extends ProcesserType{
		public static final String ID="meta";
		public MetaP(){
			put("id",MetaP.ID);
			put("name","淘宝Meta通知");
		}
		@Override
		public String getJsonObject() {
			JSONObject o=new JSONObject();
			o.put("id", new JSONString(MetaP.ID));
			o.put("config", new JSONString(""));
			return o.toString();
		}
		@Override
		public InfoConfig getInfoConfig() {
			return new DefaultInfoConfig(getName(), "TODO");
		}
	}
	
	public static class OutputCleanP extends ProcesserType{
		public static final String ID="OutputClean";
		public OutputCleanP(){
			put("id",OutputCleanP.ID);
			put("name","产出路径清理");
		}
		public void setPath(final String path){
			put("path",path);
		}
		public String getPath(){
			return get("path");
		}
		public void setDays(final Integer days){
			put("days",days==null?null:days.toString());
		}
		public Integer getDays(){
			return get("days")==null?null:Integer.valueOf(get("days"));
		}
		@Override
		public String getJsonObject() {
			JSONObject o=new JSONObject();
			o.put("id", new JSONString(OutputCleanP.ID));
			JSONObject config=new JSONObject();
			config.put("days", new JSONNumber(getDays()));
			config.put("path", new JSONString(getPath()));
			o.put("config", config);
			return o.toString();
		}
		@Override
		public InfoConfig getInfoConfig() {
			return new DefaultInfoConfig(getName(), "路径："+getPath()+"<br/>删除时间："+getDays()+" 之前");
		}
		
	}
	public static class OutputCheckP extends ProcesserType{
		public static final String ID="OutputCheck";
		public OutputCheckP(){
			put("id",OutputCheckP.ID);
			put("name","产出数据浮动检测");
		}
		public void setPercent(final Integer percent){
			put("percent",percent==null?null:percent.toString());
		}
		public void setPath(final String path){
			put("path",path);
		}
		public String getPath(){
			return get("path");
		}
		public Integer getPercent(){
			return get("percent")==null?null:Integer.valueOf(get("percent"));
		}
		@Override
		public String getJsonObject() {
			JSONObject o=new JSONObject();
			o.put("id", new JSONString(OutputCheckP.ID));
			JSONObject config=new JSONObject();
			config.put("percent", new JSONNumber(getPercent()));
			config.put("path", new JSONString(getPath()));
			o.put("config", config);
			return o.toString();
		}
		@Override
		public InfoConfig getInfoConfig() {
			return new DefaultInfoConfig(getName(), "浮动百分比："+getPercent()+"<br/>路径："+getPath());
		}
	}
	
	public static class JobP extends ProcesserType{
		public static final String ID="JobProcesser";
		private String jobId;
		private Map<String, String> kvConfig=new HashMap<String, String>();
		public JobP(){
			put("id",JobP.ID);
			put("name","自定义Job处理");
		}
		@Override
		public String getJsonObject() {
			JSONObject o=new JSONObject();
			o.put("id", new JSONString(JobP.ID));
			JSONObject config=new JSONObject();
			config.put("jobId", new JSONString(getJobId()));
			JSONObject kv=new JSONObject();
			for(String key:kvConfig.keySet()){
				if(kvConfig.get(key)!=null){
					kv.put(key, new JSONString(kvConfig.get(key)));
				}
			}
			config.put("kvConfig", kv);
			o.put("config", config);
			return o.toString();
		}
		@Override
		public InfoConfig getInfoConfig() {
			return new DefaultInfoConfig(getName(), "");
		}
		public String getJobId() {
			return jobId;
		}
		public void setJobId(final String jobId) {
			this.jobId = jobId;
			put("name","关联Job:"+jobId);
		}
		public void setJobName(final String name){
			put("name","关联Job:"+name);
		}
		public Map<String, String> getKvConfig() {
			return kvConfig;
		}
		public void setKvConfig(final Map<String, String> kvConfig) {
			this.kvConfig = kvConfig;
		}
		
	}

}
