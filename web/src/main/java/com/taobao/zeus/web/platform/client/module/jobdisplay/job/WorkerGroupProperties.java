package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.taobao.zeus.web.platform.client.util.WorkerGroupModel;

public interface WorkerGroupProperties extends PropertyAccess<WorkerGroupModel> {
	@Path("id")
	ModelKeyProvider<WorkerGroupModel> key();
	
	ValueProvider<WorkerGroupModel, String> id();
	
	ValueProvider<WorkerGroupModel, String> name();
	
	ValueProvider<WorkerGroupModel, String> description();
}
