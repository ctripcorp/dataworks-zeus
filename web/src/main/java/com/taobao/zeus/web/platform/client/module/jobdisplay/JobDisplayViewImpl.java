package com.taobao.zeus.web.platform.client.module.jobdisplay;

import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.taobao.zeus.web.platform.client.module.jobdisplay.group.GroupPresenter;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobPresenter;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

public class JobDisplayViewImpl extends CardLayoutContainer implements JobDisplayView{

	private JobPresenter jobPresenter;
	private GroupPresenter groupPresenter;
	private SimpleContainer jobContainer;
	private SimpleContainer groupContainer;
	
	private JobDisplayPresenter presenter;
	
	private PlatformContext context;
	
	public JobDisplayViewImpl(JobDisplayPresenter presenter,PlatformContext context){
		this.presenter=presenter;
		this.context=context;
		
		jobContainer=new SimpleContainer();
		groupContainer=new SimpleContainer();
		jobPresenter=new JobPresenter(context);
		groupPresenter=new GroupPresenter(context);
	}

	@Override
	public void display(JobModel job) {
		if(getWidgetIndex(jobContainer)==-1){
			add(jobContainer);
			jobPresenter.go(jobContainer);
		}
		jobPresenter.display(job);
		setActiveWidget(jobContainer);
	}

	@Override
	public void display(GroupModel group) {
		if(getWidgetIndex(groupContainer)==-1){
			add(groupContainer);
			groupPresenter.go(groupContainer);
		}
		groupPresenter.display(group);
		setActiveWidget(groupContainer);
	}

}
