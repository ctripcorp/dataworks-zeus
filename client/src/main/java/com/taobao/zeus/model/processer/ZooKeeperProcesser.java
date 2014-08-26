package com.taobao.zeus.model.processer;

import net.sf.json.JSONObject;


/**
 * ZooKeeper 处理器
 * 用于进行ZK通知
 * @author zhoufang
 *
 */
public class ZooKeeperProcesser implements Processer{

	private static final long serialVersionUID = 1L;
	private Boolean useDefault=true;
	private String host;
	private String path;
	@Override
	public String getConfig() {
		JSONObject o=new JSONObject();
		o.put("host", getHost()==null?"":getHost());
		o.put("path", getPath()==null?"":getPath());
		o.put("useDefault", getUseDefault());
		return o.toString();
	}
	
	

	@Override
	public String getId() {
		return "zookeeper";
	}

	@Override
	public void parse(String configs) {
		if("".equals(configs)){
			return;
		}
		JSONObject o=JSONObject.fromObject(configs);
		useDefault=o.getBoolean("useDefault");
		host=o.getString("host");
		path=o.getString("path");
	}

	public Boolean getUseDefault() {
		return useDefault;
	}

	public void setUseDefault(Boolean useDefault) {
		this.useDefault = useDefault;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
