package com.taobao.zeus.web.platform.client.app.user;

import java.util.Date;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobHistoryModel;
import com.taobao.zeus.web.platform.client.util.ZUser;

public interface ZUserPropertiesAction extends PropertyAccess<ZUser> {
	@Path("uid")
	ModelKeyProvider<ZUser> key();
	
	ValueProvider<ZUser, String> uid();
	
	ValueProvider<ZUser, String> name();
	
	ValueProvider<ZUser, String> email();
	
	ValueProvider<ZUser, String> phone();
	
	ValueProvider<ZUser, String> userType();
	
	ValueProvider<ZUser, String> isEffective();
	
	ValueProvider<ZUser, String> description();
	
	ValueProvider<ZUser, String> gmtModified();
}
