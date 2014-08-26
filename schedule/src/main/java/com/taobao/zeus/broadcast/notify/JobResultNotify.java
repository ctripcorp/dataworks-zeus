package com.taobao.zeus.broadcast.notify;

public interface JobResultNotify {

	void send(String historyId,String data) throws Exception;
}