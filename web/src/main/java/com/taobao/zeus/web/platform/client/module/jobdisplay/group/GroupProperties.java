package com.taobao.zeus.web.platform.client.module.jobdisplay.group;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;

public interface GroupProperties extends PropertyAccess<GroupModel>{

	ModelKeyProvider<GroupModel> key();
	
	ValueProvider<GroupModel, String> id();
	ValueProvider<GroupModel, String> name();
}
