package com.taobao.zeus.web.platform.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.taobao.zeus.web.platform.client.app.Application;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlatformPlace.KV;
import com.taobao.zeus.web.platform.client.app.document.DocumentApp;
import com.taobao.zeus.web.platform.client.app.home.HomeApp;
import com.taobao.zeus.web.platform.client.app.report.ReportApp;
import com.taobao.zeus.web.platform.client.app.schedule.ScheduleApp;
import com.taobao.zeus.web.platform.client.theme.shortcut.ShortcutCellDefaultAppearance.ShortcutCellResources;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.util.place.PlaceHandler;
import com.taobao.zeus.web.platform.client.util.place.PlatformPlaceChangeEvent;

public class Platform implements IsWidget ,PlaceHandler{

	private static PlatformUiBinder uiBinder = GWT
			.create(PlatformUiBinder.class);

	interface PlatformUiBinder extends UiBinder<Widget, Platform> {
	}
	@UiField
	Viewport viewport;
	@UiField
	BorderLayoutContainer platformContainer;
	@UiField
	VBoxLayoutContainer shortcutBar;
	@UiField
	CardLayoutContainer screen;
	
	private final PlatformContext platformContext;
	private List<Shortcut> shortcuts=new ArrayList<Shortcut>();
	public Platform(ZUser user){
		platformContext=new PlatformContext(user);
		viewport=(Viewport) uiBinder.createAndBindUi(this);
		platformContext.getPlatformBus().registPlaceHandler(this);
	}
	
	@Override
	public Widget asWidget() {
		return viewport;
	}
	
	private Map<Application, Widget> apps=new HashMap<Application, Widget>();
	public void addApp(final Application app){
		apps.put(app, null);
		if(!shortcuts.contains(app.getShortcut())){
			app.getShortcut().addSelectHandler(new SelectHandler() {
				public void onSelect(SelectEvent event) {System.out.println("SelectHandler");
					if(screen.getActiveWidget()!=null && screen.getActiveWidget().equals(apps.get(app))){
						return;
					}
					getPlatformContext().getPlatformBus().fireEvent(new PlatformPlaceChangeEvent(app.getPlace(),true));
				}
			});
			shortcuts.add(app.getShortcut());
			
			shortcutBar.add(app.getShortcut(),new BoxLayoutData(new Margins(2,0,5,0)));
		}
	}
	public void removeShortCut(Shortcut shortcut){
		shortcuts.remove(shortcut);
		shortcutBar.remove(shortcut);
	}

	public PlatformContext getPlatformContext() {
		return platformContext;
	}

	@Override
	public void handle(PlatformPlaceChangeEvent event) {
		ShortcutCellResources s=com.google.gwt.core.shared.GWT.create(ShortcutCellResources.class);
		Application app=null;
		KV kv=event.getNewPlace().getCurrent();
		if(App.Home.getKv().value.equals(kv.value)){
			app=getApp(HomeApp.class);
		}else if(App.Document.getKv().value.equals(kv.value)){
			app=getApp(DocumentApp.class);
		}else if(App.Schedule.getKv().value.equals(kv.value)){
			app=getApp(ScheduleApp.class);
		}else if(App.Report.getKv().value.equals(kv.value)){
			app=getApp(ReportApp.class);
		}
		if(app!=null){
			if(apps.get(app)==null){
				SimpleContainer container=new SimpleContainer();
				screen.add(container);
				apps.put(app,container);
				app.getPresenter().go(container);
			}
			for(Application a:apps.keySet()){
				a.getShortcut().getElement().removeClassName(s.style().select());
			}
			app.getShortcut().getElement().addClassName(s.style().select());
			screen.setActiveWidget(apps.get(app));
		}
			
	}
	private Application getApp(Class<?> clazz){
		for(Application app:apps.keySet()){
			if(app.getClass().equals(clazz)){
				return app;
			}
		}
		return null;
	}
	@Override
	public String getHandlerTag() {
		return "App";
	}


}
