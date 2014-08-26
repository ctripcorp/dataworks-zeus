package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;

public class JobPresenter implements Presenter{

	private JobView jobView=new JobView(this);
	
	private JobModel jobModel;
	
	private PlatformContext context;
	public JobPresenter(PlatformContext context){
		this.context=context;
	}
	
	public void display(JobModel job){
		this.jobModel=job;
		jobView.display();
	}
	
	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(jobView.asWidget());
	}

	public JobModel getJobModel() {
		return jobModel;
	}
	public void displayEditJob(){
		jobView.displayEditJob();
	}
	
	public void displayHistory(){
		jobView.displayHistory();
	}
	
	public void displayDepGraph(){
		jobView.displayDepGraph();
	}

	public PlatformContext getPlatformContext() {
		return context;
	}

}
