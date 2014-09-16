package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import java.util.Date;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface JobHistoryProperties extends PropertyAccess<JobHistoryModel>{
	@Path("id")
	ModelKeyProvider<JobHistoryModel> key();
	
	ValueProvider<JobHistoryModel, String> id();
	
	ValueProvider<JobHistoryModel, String> name();
	
	ValueProvider<JobHistoryModel, String> owner();
	
	ValueProvider<JobHistoryModel, String> jobId();
	
	ValueProvider<JobHistoryModel, String> toJobId();
	
	ValueProvider<JobHistoryModel, String> status();
	
	ValueProvider<JobHistoryModel, String> executeHost();
	
	ValueProvider<JobHistoryModel, String> illustrate();
	
	ValueProvider<JobHistoryModel, Date> startTime();
	
	ValueProvider<JobHistoryModel, Date> endTime();

	ValueProvider<JobHistoryModel, String> triggerType();
	
	ValueProvider<JobHistoryModel, String> operator();
	
	ValueProvider<JobHistoryModel, String> statisEndTime();
	
	ValueProvider<JobHistoryModel, String> timeZone();
	
	ValueProvider<JobHistoryModel, String> cycle();
	
}
