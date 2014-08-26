package com.taobao.zeus.web.platform.client.app.schedule;

import com.taobao.zeus.web.platform.client.app.Application;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class ScheduleApp implements Application{

	private SchedulePresenter presenter;
	private PlatformContext platformContext;
	private ScheduleShortcut shortcut=new ScheduleShortcut();
	public ScheduleApp(PlatformContext context){
		this.platformContext=context;
		presenter=new SchedulePresenterImpl(context);
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
		return new PlacePath().toApp(App.Schedule).create();
	}

	public static final String TAG="schedule";
}
