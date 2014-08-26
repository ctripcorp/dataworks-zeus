package com.taobao.zeus.model.processer;

import net.sf.json.JSONObject;
/**
 * 
 * 后置单元，用来删除过期文件
 * days: 删除几天以上的文件
 * path: 当前job的产出路径
 * @author zhoufang
 *
 */
public class OutputCleanProcesser implements Processer{

	private static final long serialVersionUID = 1L;
	private int days=7;
	private String path;
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getConfig() {
		JSONObject o=new JSONObject();
		o.put("path", path);
		o.put("days", days);
		return o.toString();
	}

	@Override
	public String getId() {
		return "OutputClean";
	}

	@Override
	public void parse(String config) {
		JSONObject o=JSONObject.fromObject(config);
		path=o.getString("path");
		days=o.getInt("days");
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

}
