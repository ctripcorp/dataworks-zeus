package com.taobao.zeus.web.platform.client.module.jobmanager;

import com.google.gwt.user.client.ui.IsWidget;

public interface JobManagerView extends IsWidget{
	public JobTree getMyTreePanel();
	public JobTree getAllTreePanel();
	public boolean isMyTreeActive();
	public void activeMyTreePanel();
	public void activeAllTreePanel();
}
