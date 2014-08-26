package com.taobao.zeus.web.platform.client.util.async;

public class CallbackUtils {

	public static PlatformAsyncCallback BLANK=new PlatformAsyncCallback() {
		@Override
		public void callback(Object o) {
			return;
		}
	};
	
}
