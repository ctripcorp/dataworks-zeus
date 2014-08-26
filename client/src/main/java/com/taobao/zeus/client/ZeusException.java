package com.taobao.zeus.client;

public class ZeusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ZeusException(){
		super();
	}
	
	public ZeusException(String message){
		super(message);
	}
	
	public ZeusException(Throwable e){
		super(e);
	}
	
	public ZeusException(String msg,Throwable e){
		super(msg, e);
	}

}
