package com.taobao.zeus.model.processer;
/**
 * 已经废弃，Meta消息将会默认发送，无需配置
 * @author zhoufang
 *
 */
@Deprecated
public class MetaProcesser implements Processer{

	private static final long serialVersionUID = 1L;

	@Override
	public String getConfig() {
		return "";
	}

	@Override
	public String getId() {
		return "meta";
	}

	@Override
	public void parse(String config) {
		
	}

}
