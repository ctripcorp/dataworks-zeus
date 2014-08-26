package com.taobao.zeus.web.platform.client.util;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class StartEvent extends GwtEvent<StartEvent.StartEventHandler>{

	public interface StartEventHandler extends EventHandler{
		public void start();
	}

	public static Type<StartEventHandler> TYPE=new Type<StartEventHandler>();
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<StartEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(StartEventHandler handler) {
		handler.start();
	}
}
