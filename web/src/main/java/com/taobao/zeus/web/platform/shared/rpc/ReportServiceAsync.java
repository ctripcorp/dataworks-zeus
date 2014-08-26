package com.taobao.zeus.web.platform.shared.rpc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ReportServiceAsync {

	void runningJobs(Date start, Date end,
			AsyncCallback<List<Map<String, String>>> callback);

	void ownerFailJobs(Date date,
			AsyncCallback<List<Map<String, String>>> callback);

}
