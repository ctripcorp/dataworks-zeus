package com.taobao.zeus.model.processer;

import net.sf.json.JSONObject;

/**
 * 邮件通知
 * @author zhoufang
 *
 */
public class MailProcesser implements Processer{
	private static final long serialVersionUID = 1L;
	private String subject;
	private String template;
	@Override
	public String getConfig() {
		JSONObject o=new JSONObject();
		o.put("subject", getSubject());
		o.put("template", getTemplate());
		return o.toString();
	}

	@Override
	public String getId() {
		return "mail";
	}

	@Override
	public void parse(String configs) {
		JSONObject o=JSONObject.fromObject(configs);
		setTemplate(o.getString("template"));
		setSubject(o.getString("subject"));
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	
}
