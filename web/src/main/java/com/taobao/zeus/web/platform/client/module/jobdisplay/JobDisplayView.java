package com.taobao.zeus.web.platform.client.module.jobdisplay;

import com.google.gwt.user.client.ui.IsWidget;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;

public interface JobDisplayView extends IsWidget{

	
	public void display(JobModel job);
	
	public void display(GroupModel group);
}
