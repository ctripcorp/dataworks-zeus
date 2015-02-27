package com.taobao.zeus.web.platform.client.app.user;

import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.app.Application;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class UserApp implements Application{

	private UserShortcut shortcut=new UserShortcut();
	private PlatformContext context;
	public UserApp(PlatformContext context){
		this.context=context;
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
		return new Presenter() {
			public void go(HasWidgets hasWidgets) {
				hasWidgets.add(widget);
				widget.setContent("<div><script language=javascript>window.location.href='/zeus-web/userInfo.html'</script></div>");
			}
			@Override
			public PlatformContext getPlatformContext() {
				return null;
			}
		};
	}
	@Override
	public PlatformPlace getPlace() {
		return new PlacePath().toApp(App.User).create();
	}

	public static final String TAG="user";
}
