package com.taobao.zeus.web.platform.client.app.schedule;

import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.module.jobdisplay.JobDisplayPresenterImpl;
import com.taobao.zeus.web.platform.client.module.jobdisplay.JobDisplayPresenter;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobManagerPresenter;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobManagerPresenterImpl;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

public class SchedulePresenterImpl implements SchedulePresenter {

	private JobManagerPresenter jobManagerPresenter;
	private JobDisplayPresenter jobDisplayPresenter;
	
	private ScheduleView scheduleView;
	private final PlatformContext context;
	public SchedulePresenterImpl(PlatformContext context){
		this.context=context;
	}
	
	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(getScheduleView().asWidget());
	}

	@Override
	public JobDisplayPresenter getJobDisplayPresenter() {
		if(jobDisplayPresenter==null){
			jobDisplayPresenter=new JobDisplayPresenterImpl(context);
		}
		return jobDisplayPresenter;
	}

	@Override
	public JobManagerPresenter getJobManagerPresenter() {
		if(jobManagerPresenter==null){
			jobManagerPresenter=new JobManagerPresenterImpl(context);
		}
		return jobManagerPresenter;
	}

	public ScheduleView getScheduleView() {
		if(scheduleView==null){
			scheduleView=new ScheduleViewImpl(this);
		}
		return scheduleView;
	}

	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}

}
