package com.taobao.zeus.web.platform.client.app.document;

import com.taobao.zeus.web.platform.client.app.Application;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class DocumentApp implements Application{

	private PlatformContext platformContext;
	private DocumentShortcut shortcut=new DocumentShortcut();
	private DocumentPresenter presenter;
	public DocumentApp(PlatformContext context){
		this.platformContext=context;
		presenter=new DocumentPresenterImpl(context);
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
		return new PlacePath().toApp(App.Document).create();
	}
	
	public static final String TAG="doc";
}
