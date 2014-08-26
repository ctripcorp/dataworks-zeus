package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;

public class JobView extends CardLayoutContainer implements IsWidget{

	private CardInfo info;
	private CardHistory history;
	private CardEditJob editJob;
	private CardDepGraph depGraph;
	
	private JobPresenter presenter;
	public JobView(JobPresenter presenter){
		this.presenter=presenter;
		editJob=new CardEditJob(presenter);
		info=new CardInfo(presenter);
		history=new CardHistory(presenter);
		depGraph=new CardDepGraph(presenter);
		add(info);
		add(history);
		add(editJob);
		add(depGraph);
	}

	public void display(){
		setActiveWidget(info);
		info.setHeadingText(getBaseTitle());
		info.refresh(presenter.getJobModel());
		info.getCenter().getElement().setScrollTop(0);
	}
	
	public void displayEditJob(){
		setActiveWidget(editJob);
		editJob.refresh(presenter.getJobModel());
		editJob.getCenter().getElement().setScrollTop(0);
		editJob.setHeadingText(getBaseTitle()+" > 编辑任务");
	}
	
	public void displayHistory(){
		setActiveWidget(history);
		history.refresh(presenter.getJobModel());
		history.setHeadingText(getBaseTitle()+" > 历史运行日志");
	}
	
	private String getBaseTitle(){
		return "任务："+presenter.getJobModel().getName()+"("+presenter.getJobModel().getId()+")";
	}

	public void displayDepGraph() {
		setActiveWidget(depGraph);
		depGraph.setHeadingText(getBaseTitle()+" > 依赖图");
		depGraph.refresh(presenter.getJobModel());
	}
}
