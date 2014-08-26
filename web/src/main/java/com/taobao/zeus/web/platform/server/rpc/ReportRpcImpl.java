package com.taobao.zeus.web.platform.server.rpc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.zeus.store.mysql.MysqlReportManager;
import com.taobao.zeus.web.platform.shared.rpc.ReportService;

public class ReportRpcImpl implements ReportService{

	@Autowired
	private MysqlReportManager reportManager;
	private SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat displayFormat=new SimpleDateFormat("MM月dd日");
	@Override
	public List<Map<String, String>> runningJobs(Date start, Date end) {
		Map<String, Map<String, String>> map=reportManager.runningJobs(start, end);
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(start);
		List<Map<String, String>> result=new ArrayList<Map<String,String>>();
		do{
			Map<String, String> data=map.get(format.format(calendar.getTime()));
			if(data==null){
				data=new HashMap<String, String>();
				data.put("success", "0");
				data.put("fail", "0"); 
			}
			data.put("date", displayFormat.format(calendar.getTime()));
			result.add(data);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}while(calendar.getTime().before(end));
		return result;
	}
	@Override
	public List<Map<String, String>> ownerFailJobs(Date date) {
		return reportManager.ownerFailJobs(date);
	}

}
