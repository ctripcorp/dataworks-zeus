package com.taobao.zeus.model.processer;
/**
 * 旺旺通知
 * @author zhoufang
 *
 */
public class WangWangProcesser implements Processer{

	private static final long serialVersionUID = 1L;
	private String template;
	@Override
	public String getConfig() {
		return getTemplate();
	}

	@Override
	public String getId() {
		return "wangwang";
	}

	@Override
	public void parse(String config) {
		setTemplate(config);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
