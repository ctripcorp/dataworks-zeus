package com.taobao.zeus.web.platform.client.app.schedule;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

public class ScheduleViewImpl implements ScheduleView {

	private SchedulePresenter presenter;
	public ScheduleViewImpl(SchedulePresenter presenter){
		this.presenter=presenter;
	}
	private Widget widget;
	@Override
	public Widget asWidget() {
		if(widget==null){
			final BorderLayoutContainer border = new BorderLayoutContainer();
			BorderLayoutData west = new BorderLayoutData();
			west.setMargins(new Margins(3));
			west.setSplit(true);
			west.setSize(300);
			west.setCollapsible(true);
			west.setCollapseMini(true);
			ContentPanel sc=new ContentPanel();
			sc.setHeaderVisible(false);
			SimpleContainer sc2=new SimpleContainer();
			border.setWestWidget(sc, west);
			border.setCenterWidget(sc2, new MarginData(3));
			presenter.getJobManagerPresenter().go(sc);
			presenter.getJobDisplayPresenter().go(sc2);
			widget=border;
		}
		return widget;
	}


}
