package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.taobao.zeus.web.platform.client.util.HostGroupModel;

public interface HostGroupProperties extends PropertyAccess<HostGroupModel> {
	@Path("id")
	ModelKeyProvider<HostGroupModel> key();
	
	ValueProvider<HostGroupModel, String> id();
	
	ValueProvider<HostGroupModel, String> name();
	
	ValueProvider<HostGroupModel, String> description();
}
