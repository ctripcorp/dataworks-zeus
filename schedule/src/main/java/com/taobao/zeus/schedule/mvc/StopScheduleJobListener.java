package com.taobao.zeus.schedule.mvc;

import com.taobao.zeus.mvc.DispatcherListener;
import com.taobao.zeus.mvc.MvcEvent;
import com.taobao.zeus.schedule.mvc.event.Events;

/**
 * 阻止Job任务进行自动调度(包含自动调度和手动恢复)
 * 预发环境下使用
 * 预发环境不允许运行自动调度，手动恢复，只能运行手动调度
 * @author zhoufang
 *
 */
public class StopScheduleJobListener extends DispatcherListener{

	@Override
	public void beforeDispatch(MvcEvent mvce) {
		if(mvce.getAppEvent().getType()==Events.Initialize){
			//取消初始化事件，放置Job进行出错任务重试，以及开启定时器
			mvce.setCancelled(true);
		}
	}
}
