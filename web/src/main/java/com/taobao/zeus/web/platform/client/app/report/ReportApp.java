package com.taobao.zeus.web.platform.client.app.report;

import com.taobao.zeus.web.platform.client.app.Application;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class ReportApp implements Application{

	private ReportPresenter presenter;
	private ReportShortcut shortcut=new ReportShortcut();
	private PlatformContext platformContext;
	public ReportApp(PlatformContext context){
		this.platformContext=context;
		presenter=new ReportPresenterImpl(context);
	}
	@Override
	public Shortcut getShortcut() {
		return shortcut;
	}

	@Override
	public PlatformContext getPlatformContext() {
		return platformContext;
	}

	@Override
	public Presenter getPresenter() {
		return presenter;
	}
	@Override
	public PlatformPlace getPlace() {
		return new PlacePath().toApp(App.Report).create();
	}
	
	public static final String TAG="report";
}
