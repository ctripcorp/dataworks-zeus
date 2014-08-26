package com.taobao.zeus.web.platform.client.app.report;

import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

public class ReportPresenterImpl implements ReportPresenter{

	private ReportView reportView;
	private PlatformContext context;
	public ReportPresenterImpl(PlatformContext context){
		this.context=context;
		reportView=new ReportViewImpl(this);
	}
	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(reportView.asWidget());
	}

	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}

}
