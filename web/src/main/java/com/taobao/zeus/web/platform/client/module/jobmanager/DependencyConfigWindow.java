package com.taobao.zeus.web.platform.client.module.jobmanager;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.Window;

public class DependencyConfigWindow extends Window{
	
	private TabPanel tabPanel;
	private JobTreePanel checkablePanel;
	private JobTreePanel copyPanel;
	private String jobId;
	
	public DependencyConfigWindow(String jobId){
		this.jobId = jobId;
		Init();
	}
	private void Init() {
		setHeight(600);
		setWidth(500);
		tabPanel = new TabPanel();
		tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
	        @Override
	        public void onSelection(SelectionEvent<Widget> event) {
	        	Widget selectedItem = event.getSelectedItem();
	        	if (selectedItem instanceof JobTreePanel) {
					JobTreePanel panel = (JobTreePanel)selectedItem;
					if (!panel.isHasData() && !panel.isShowAll()) {
						panel.loadDataOfOtherDependentJob(jobId);
					}
				}
	        }
	      });
		checkablePanel = new JobTreePanel(true);
		checkablePanel.setHeadingText("选择依赖任务(可以多选)");
		copyPanel = new JobTreePanel(false);
		copyPanel.setHeadingText("复制其它依赖任务的依赖到本任务");
		copyPanel.getTree().setCheckable(false);
		copyPanel.getTree().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tabPanel.add(checkablePanel,new TabItemConfig("选择依赖任务",false));
		tabPanel.add(copyPanel,new TabItemConfig("复制依赖到本任务",false));
		add(tabPanel);
	}
	
	public JobTreePanel getCheckablePanel() {
		return checkablePanel;
	}
	public JobTreePanel getCopyPanel() {
		return copyPanel;
	}
}
