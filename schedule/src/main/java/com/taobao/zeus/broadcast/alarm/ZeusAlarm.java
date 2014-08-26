package com.taobao.zeus.broadcast.alarm;

import com.taobao.zeus.schedule.mvc.JobFailListener.ChainException;

public interface ZeusAlarm {

	void alarm(String historyId,String title,String content,ChainException chain) throws Exception;

	void alarm(String historyId,String title,String content) throws Exception;
}