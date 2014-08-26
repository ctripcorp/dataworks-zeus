package com.taobao.zeus.web.platform.shared.rpc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("report.rpc")
public interface ReportService extends RemoteService{
	/**
	 * 按日排序的任务运行趋势数据
	 * 包括每天自动调度成功的任务数，每天自动调度失败的任务数
	 * @return
	 */
	List<Map<String, String>> runningJobs(Date start,Date end);
	/**
	 * 负责人的失败任务统计
	 * @param date
	 * @return
	 */
	List<Map<String, String>> ownerFailJobs(Date date);
}
