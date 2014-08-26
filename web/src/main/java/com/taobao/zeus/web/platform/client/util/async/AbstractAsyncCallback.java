package com.taobao.zeus.web.platform.client.util.async;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractAsyncCallback<T> implements AsyncCallback<T>{

	@Override
	public void onFailure(Throwable caught) {
		Window.alert(caught.getMessage());
		caught.printStackTrace();
	}

}
