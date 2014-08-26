package com.taobao.zeus.web.platform.client.util.async;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class PlatformAsyncCallback<T>{

	public abstract void callback(T t);
	
	public void exception(Exception e){
		Window.alert(e.getCause().getMessage());
	}
}
