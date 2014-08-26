package com.taobao.zeus.web.platform.client.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.History;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.util.place.PlaceHandler;
import com.taobao.zeus.web.platform.client.util.place.PlatformPlaceChangeEvent;

public class PlatformBus {

	private SimpleEventBus eventBus = new SimpleEventBus();
	private Map<String, PlaceHandler> handlerMap=new HashMap<String, PlaceHandler>();
	
	public PlatformBus(){
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();
				eventBus.fireEvent(new PlatformPlaceChangeEvent(new PlatformPlace(token)));
			}
		});
		eventBus.addHandler(PlatformPlaceChangeEvent.TYPE, new PlatformPlaceChangeEvent.Handler() {
			public void onPlaceChange(PlatformPlaceChangeEvent event) {
				PlatformPlace pp=event.getNewPlace();
				if(pp.getCurrent()!=null){
					if(event.isLogHistory() && pp.isOriginal()){
						event.setLogHistory(false);
						History.newItem(pp.getToken(), false);
					}
					String key=pp.getCurrent().key;
					PlaceHandler handler=handlerMap.get(key);
					if(handler!=null){
						handler.handle(event);
					}
					if(!event.isAsyncCall()){
						if(pp.next()){
							onPlaceChange(event);
						}
					}
				}
			}
		});
		eventBus.addHandler(StartEvent.TYPE, new StartEvent.StartEventHandler() {
			public void start() {
				if(History.getToken()!=null && !"".equals(History.getToken().trim())){
					eventBus.fireEvent(new PlatformPlaceChangeEvent(new PlatformPlace(History.getToken())));
				}else{
					eventBus.fireEvent(new PlatformPlaceChangeEvent(new PlacePath().toApp(App.Home).create(),true));
				}
			}
		});
	}

	public HandlerRegistration addHandler(Event.Type type, EventHandler handler) {
		return eventBus.addHandler(type, handler);
	}

	public void fireEvent(GwtEvent<? extends EventHandler> event) {
		eventBus.fireEvent(event);
	}

	public void registPlaceHandler(PlaceHandler handler){
		handlerMap.put(handler.getHandlerTag(), handler);
	}
}
