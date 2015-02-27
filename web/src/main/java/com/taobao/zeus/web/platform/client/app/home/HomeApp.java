package com.taobao.zeus.web.platform.client.app.home;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.app.Application;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.util.GWTEnvironment;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.util.template.TemplateResources;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class HomeApp implements Application{

	private HomeShortcut shortcut=new HomeShortcut();
	private PlatformContext context;
	public HomeApp(PlatformContext context){
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

	private HomeWidget widget=new HomeWidget();
	@Override
	public Presenter getPresenter() {
		return new Presenter() {
			public void go(HasWidgets hasWidgets) {
				hasWidgets.add(widget);
				String id=GWTEnvironment.getHomeTemplateId();
				RPCS.getFileManagerService().getHomeFile(id, new AbstractAsyncCallback<FileModel>() {
					public void onSuccess(FileModel result) {
						widget.setContent(result.getContent());
					}
					@Override
					public void onFailure(Throwable caught) {
						TemplateResources templates=GWT.create(TemplateResources.class);
						widget.setContent(templates.home().getText());
					}
				});
			}
			@Override
			public PlatformContext getPlatformContext() {
				return null;
			}
		};
	}
	@Override
	public PlatformPlace getPlace() {
		return new PlacePath().toApp(App.Home).create();
	}

	public static final String TAG="home";
}
