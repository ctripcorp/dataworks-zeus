package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.taobao.zeus.web.platform.client.lib.jit.SpaceTree;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.Refreshable;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class CardDepGraph extends CenterTemplate implements Refreshable<JobModel>{

	private JobPresenter presenter;
	
	
	private SpaceTree dependeeST;
	private SpaceTree dependerST;
	
	public CardDepGraph(JobPresenter p){
		this.presenter=p;
		
		addButton(new TextButton("返回",new SelectHandler() {
			public void onSelect(SelectEvent event) {
				presenter.display(presenter.getJobModel());
			}
		}));
		
		FlowLayoutContainer container=new FlowLayoutContainer();
		container.setScrollMode(ScrollMode.AUTOY);
		
//		FieldSet dependeeField=new FieldSet();
//		dependeeField.setHeight(400);
//		dependeeField.setHeadingText("任务依赖图");
		dependeeST=new SpaceTree("bottom");
		dependeeST.setBorders(true);
//		dependeeField.add(dependeeST);
		
//		FieldSet dependerField=new FieldSet();
//		dependerField.setHeight(400);
//		dependerField.setHeadingText("任务被依赖图");
		dependerST=new SpaceTree("top");
		dependerST.setBorders(true);
//		dependerField.add(dependerST);
		
//		container.add(dependeeField);
//		container.add(dependerField);
		
		container.add(new HTMLPanel("任务依赖图："));
		container.add(dependeeST);
		container.add(new HTMLPanel("任务被依赖图："));
		container.add(dependerST);
		
		setCenter(container);
	}
	@Override
	public void refresh(JobModel t) {
		dependeeST.mask("加载中，请稍等");
		RPCS.getTreeService().getDependeeTreeJson(t.getId(), new AbstractAsyncCallback<String>() {
			public void onSuccess(String result) {
				dependeeST.loadData(result);
				dependeeST.unmask();
			}
		});
		dependerST.mask("加载中，请稍等");
		RPCS.getTreeService().getDependerTreeJson(t.getId(), new AbstractAsyncCallback<String>(){
			public void onSuccess(String result) {
				dependerST.loadData(result);
				dependerST.unmask();
			}
			
		});
	}

}
