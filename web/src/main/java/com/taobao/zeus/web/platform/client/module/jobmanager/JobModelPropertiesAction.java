package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.util.Map;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface JobModelPropertiesAction extends PropertyAccess<JobModelAction>{
	@Path("id")
	ModelKeyProvider<JobModelAction> key();
	
	ValueProvider<JobModelAction, String> id();
	
	ValueProvider<JobModelAction, String> toJobId();
	
	ValueProvider<JobModelAction, String> name();
	
	ValueProvider<JobModelAction, String> status();
	
	ValueProvider<JobModelAction, Map<String, String>> readyDependencies();
	
	ValueProvider<JobModelAction, String> lastStatus();
}
