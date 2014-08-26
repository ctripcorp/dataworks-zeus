package com.taobao.zeus.web.platform.client.module.word.model;

import java.util.Date;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryModel.JobRunType;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryModel.Status;

public interface DebugHistoryProperties extends PropertyAccess<DebugHistoryModel> {
	
	@Path("id")
	ModelKeyProvider<DebugHistoryModel> key();
	
	ValueProvider<DebugHistoryModel,String> id();
	
	ValueProvider<DebugHistoryModel,String> fileId();

	ValueProvider<DebugHistoryModel,Date> startTime();

	ValueProvider<DebugHistoryModel,Date> endTime();

	ValueProvider<DebugHistoryModel,String> executeHost();

	ValueProvider<DebugHistoryModel,Date> gmtCreate();

	ValueProvider<DebugHistoryModel,Date> gmtModified();

	ValueProvider<DebugHistoryModel,Status> status();

	ValueProvider<DebugHistoryModel,String> script();

	ValueProvider<DebugHistoryModel,JobRunType> jobRunType();

	ValueProvider<DebugHistoryModel,String> log();
	
}
