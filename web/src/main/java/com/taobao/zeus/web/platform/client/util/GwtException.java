package com.taobao.zeus.web.platform.client.util;

public class GwtException extends Exception{

	private static final long serialVersionUID = 1L;

	public GwtException() {
	}
	public GwtException(String msg){
		super(msg);
	}
	public GwtException(String msg,Throwable t){
		super(msg, t);
	}
	public GwtException(Throwable t){
		super(t);
	}
}
