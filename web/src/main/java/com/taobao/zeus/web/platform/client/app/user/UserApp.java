package com.taobao.zeus.web.platform.client.app.user;

import com.taobao.zeus.web.platform.client.app.Application;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class UserApp implements Application{

	private UserShortcut shortcut=new UserShortcut();
	private PlatformContext context;
	private UserPresenter presenter;
	public UserApp(PlatformContext context, ZUser zuser){
		this.context=context;
		presenter = new UserPresenter(context, zuser);
	}
	@Override
	public Shortcut getShortcut() {
		return shortcut;
	}

	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}

	private UserWidget widget=new UserWidget();
	@Override
	public Presenter getPresenter() {
		return presenter;
	}
	@Override
	public PlatformPlace getPlace() {
		return new PlacePath().toApp(App.User).create();
	}

	public static final String TAG="user";
}
