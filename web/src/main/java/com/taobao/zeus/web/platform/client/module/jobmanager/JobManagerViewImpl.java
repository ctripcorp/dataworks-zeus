package com.taobao.zeus.web.platform.client.module.jobmanager;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;

public class JobManagerViewImpl implements JobManagerView{

	private AccordionLayoutContainer container;
	
	private JobTree myTreePanel;
	private JobTree allTreePanel;
	private JobManagerPresenter presenter;
	public JobManagerViewImpl(final JobManagerPresenter presenter){
		this.presenter=presenter;
	}
	
	@Override
	public Widget asWidget() {
		return getContainer();
	}

	public AccordionLayoutContainer getContainer() {
		if(container==null){
			container=new AccordionLayoutContainer();
			container.add(getMyTreePanel());
			container.add(getAllTreePanel());
			container.setActiveWidget(getMyTreePanel());
		}
		return container;
	}

	public JobTree getMyTreePanel() {
		if(myTreePanel==null){
			myTreePanel=new JobTree(presenter,true){
				protected void onCollapse() {
					super.onCollapse();
					getTree().getSelectionModel().deselectAll();
				};
			};
			myTreePanel.setHeadingText("我的调度任务");
		}
		return myTreePanel;
	}

	public JobTree getAllTreePanel() {
		if(allTreePanel==null){
			allTreePanel=new JobTree(presenter,false){
				protected void onCollapse() {
					super.onCollapse();
					getTree().getSelectionModel().deselectAll();
				};
			};
			allTreePanel.setHeadingText("所有的调度任务");
		}
		return allTreePanel;
	}

	@Override
	public void activeMyTreePanel() {
		getContainer().setActiveWidget(getMyTreePanel());
	}

	@Override
	public void activeAllTreePanel() {
		getContainer().setActiveWidget(getAllTreePanel());
	}

	@Override
	public boolean isMyTreeActive() {
		return getContainer().getActiveWidget()==getMyTreePanel();
	}

}
