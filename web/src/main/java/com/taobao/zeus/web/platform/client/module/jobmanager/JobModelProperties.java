package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.util.Map;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface JobModelProperties extends PropertyAccess<JobModel>{
	@Path("id")
	ModelKeyProvider<JobModel> key();
	
	ValueProvider<JobModel, String> id();
	
	ValueProvider<JobModel, String> name();
	
	ValueProvider<JobModel, String> status();
	
	ValueProvider<JobModel, Map<String, String>> readyDependencies();
	
	ValueProvider<JobModel, String> lastStatus();
}
