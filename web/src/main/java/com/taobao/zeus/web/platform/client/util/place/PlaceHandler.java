package com.taobao.zeus.web.platform.client.util.place;

public interface PlaceHandler {

	public void handle(PlatformPlaceChangeEvent event);
	
	public String getHandlerTag();
}
