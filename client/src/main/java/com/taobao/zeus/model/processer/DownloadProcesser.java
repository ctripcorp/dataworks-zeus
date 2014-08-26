package com.taobao.zeus.model.processer;
/**
 * 下载资源文件
 * @author zhoufang
 *
 */
public class DownloadProcesser implements Processer{

	private static final long serialVersionUID = 1L;

	@Override
	public String getConfig() {
		return "";
	}

	@Override
	public String getId() {
		return "download";
	}

	@Override
	public void parse(String configs) {
		
	}
}
