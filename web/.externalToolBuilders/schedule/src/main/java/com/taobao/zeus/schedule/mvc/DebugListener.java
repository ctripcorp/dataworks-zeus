package com.taobao.zeus.schedule.mvc;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.taobao.zeus.model.DebugHistory;
import com.taobao.zeus.model.FileDescriptor;
import com.taobao.zeus.mvc.DispatcherListener;
import com.taobao.zeus.mvc.MvcEvent;
import com.taobao.zeus.schedule.mvc.event.DebugFailEvent;
import com.taobao.zeus.schedule.mvc.event.DebugSuccessEvent;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.store.FileManager;
/**
 * 任务失败的监听
 * 当任务失败，需要发送邮件给相关人员
 * @author zhoufang
 *
 */
public class DebugListener extends DispatcherListener{
	private static Logger log=LogManager.getLogger(DebugListener.class);
	
	private FileManager fileManager;
	public DebugListener(MasterContext context){
		fileManager=(FileManager) context.getApplicationContext().getBean("fileManager");
	}
	@Override
	public void beforeDispatch(MvcEvent mvce) {
		try {
			if(mvce.getAppEvent() instanceof DebugFailEvent){
				final DebugFailEvent event=(DebugFailEvent) mvce.getAppEvent();
				DebugHistory history=event.getHistory();
				FileDescriptor fd=fileManager.getFile(history.getFileId());
				
				String msg="调试任务:"+fd.getName()+" 运行失败";
				//此处可以发送IM消息
			}else if(mvce.getAppEvent() instanceof DebugSuccessEvent){
				final DebugSuccessEvent event=(DebugSuccessEvent) mvce.getAppEvent();
				DebugHistory history=event.getHistory();
				FileDescriptor fd=fileManager.getFile(history.getFileId());
				
				String msg="调试任务:"+fd.getName()+" 运行成功";
				//此处可以发送IM消息
			}
		} catch (Exception e) {
			//处理异常，防止后续的依赖任务受此影响，无法正常执行
			log.error("失败任务，发送通知出现异常",e);
		}
	}
}
