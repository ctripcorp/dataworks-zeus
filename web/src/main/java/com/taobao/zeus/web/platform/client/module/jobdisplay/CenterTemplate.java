package com.taobao.zeus.web.platform.client.module.jobdisplay;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.CellButtonBase;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;

public class CenterTemplate extends ContentPanel{

	protected VBoxLayoutContainer buttonArea = new VBoxLayoutContainer();
	protected BorderLayoutContainer container=new BorderLayoutContainer();
	
	public CenterTemplate(){
		buttonArea.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		
		BorderLayoutData east=new BorderLayoutData(100);
		east.setMargins(new Margins(5));
		container.setEastWidget(buttonArea, east);
		add(container);
	}
	
	public Widget getCenter(){
		return container.getCenterWidget();
	}
	public void setCenter(Widget widget){
		container.setCenterWidget(widget,new MarginData(3));
	}
	
	public void addButton(CellButtonBase<?> btn){
		BoxLayoutData data=new BoxLayoutData(new Margins(5));
		buttonArea.add(btn,data);
	}
}
