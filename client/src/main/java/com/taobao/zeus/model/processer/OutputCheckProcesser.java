package com.taobao.zeus.model.processer;

import net.sf.json.JSONObject;

/**
 * 云梯产出数据大小验证
 * @author zhoufang
 *
 */
public class OutputCheckProcesser implements Processer{

	private static final long serialVersionUID = 1L;

	private Integer percent=10;
	private String path;
	@Override
	public String getConfig() {
		JSONObject o=new JSONObject();
		o.put("percent", percent);
		o.put("path", path);
		return o.toString();
	}

	@Override
	public String getId() {
		return "OutputCheck";
	}

	@Override
	public void parse(String config) {
		JSONObject o=JSONObject.fromObject(config);
		percent=o.getInt("percent");
		path=o.getString("path");
	}

	public Integer getPercent() {
		return percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
