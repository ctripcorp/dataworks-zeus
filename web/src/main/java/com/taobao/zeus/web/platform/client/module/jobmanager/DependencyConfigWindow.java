package com.taobao.zeus.web.platform.client.module.jobmanager;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.Window;

public class DependencyConfigWindow extends Window{
	private TabPanel tabPanel;
	private JobTreePanel checkablePanel;
	private JobTreePanel copyPanel;
	public DependencyConfigWindow(){
		setHeight(600);
		setWidth(500);
		tabPanel = new TabPanel();
		checkablePanel = new JobTreePanel();
		copyPanel = new JobTreePanel();
		
		checkablePanel.setHeadingText("选择依赖任务(可以多选)");
		copyPanel.setHeadingText("复制其它任务的依赖到本任务");
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
