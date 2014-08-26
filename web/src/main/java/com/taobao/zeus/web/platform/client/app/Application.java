package com.taobao.zeus.web.platform.client.app;

import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public interface Application {
	
	public static final String TAG="App";

	public Shortcut getShortcut();
	
	public PlatformContext getPlatformContext();
	
	public Presenter getPresenter();
	
	public PlatformPlace getPlace();
}
