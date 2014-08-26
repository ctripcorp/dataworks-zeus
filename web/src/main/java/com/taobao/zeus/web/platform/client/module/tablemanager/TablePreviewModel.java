package com.taobao.zeus.web.platform.client.module.tablemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.taobao.zeus.web.platform.client.module.tablemanager.component.Tuple;

/**
 * 表数据预览
 * 
 * @author zhoufang
 * 
 */
public class TablePreviewModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<String> headers = new ArrayList<String>();

	private List<Tuple<Integer, List<String>>> data = new ArrayList<Tuple<Integer, List<String>>>();

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public List<Tuple<Integer, List<String>>> getData() {
		return data;
	}

	public void setData(List<Tuple<Integer, List<String>>> data) {
		this.data = data;
	}
}
