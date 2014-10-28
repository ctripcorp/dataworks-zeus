package com.taobao.zeus.broadcast.notify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.JobHistoryManager;

public abstract class AbstractJobResultNotify implements JobResultNotify{
	@Autowired
	protected JobHistoryManager jobHistoryManager;
	@Autowired
	@Qualifier("groupManager")
	protected GroupManager groupManager;

}