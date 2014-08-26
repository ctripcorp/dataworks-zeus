package com.taobao.zeus.web;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.netty.channel.Channel;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.zeus.mvc.Controller;
import com.taobao.zeus.mvc.Dispatcher;
import com.taobao.zeus.schedule.DistributeLocker;
import com.taobao.zeus.schedule.ZeusSchedule;
import com.taobao.zeus.socket.master.JobElement;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.socket.master.MasterWorkerHolder;
import com.taobao.zeus.socket.master.MasterWorkerHolder.HeartBeatInfo;
/**
 * Dump调度系统内的Job状态，用来调试排查问题
 * @author zhoufang
 *
 */
public class ScheduleDump extends HttpServlet  {

	private static final long serialVersionUID = 1L;
	private DistributeLocker locker;
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ApplicationContext context=WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		locker=(DistributeLocker) context.getBean("distributeLocker");
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			if(locker!=null){
				Field zeusScheduleField=locker.getClass().getDeclaredField("zeusSchedule");
				zeusScheduleField.setAccessible(true);
				ZeusSchedule zeusSchedule=(ZeusSchedule) zeusScheduleField.get(locker);
				if(zeusSchedule!=null){
					Field masterContextField=zeusSchedule.getClass().getDeclaredField("context");
					masterContextField.setAccessible(true);
					MasterContext context=(MasterContext)masterContextField.get(zeusSchedule);
					if(context!=null){
						String op=req.getParameter("op");
						if("workers".equals(op)){
							Map<Channel, MasterWorkerHolder> workers=context.getWorkers();
							SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							for(Channel channel:workers.keySet()){
								MasterWorkerHolder holder=workers.get(channel);
								Set<String> runnings=holder.getRunnings().keySet();
								Set<String> manualRunnings=holder.getManualRunnings().keySet();
								Set<String> debugRunnings=holder.getDebugRunnings().keySet();
								HeartBeatInfo heart=holder.getHeart();
								resp.getWriter().println(channel.getRemoteAddress()+":");
								resp.getWriter().println("\t runnings:"+runnings.toString());
								resp.getWriter().println("\t manual runnings:"+manualRunnings.toString());
								resp.getWriter().println("\t debug runnings:"+debugRunnings.toString());
								resp.getWriter().println("\t heart beat: ");
								resp.getWriter().println("\t\t last heartbeat:"+ format.format(heart.timestamp));
								resp.getWriter().println("\t\t mem use rate:"+heart.memRate);
								resp.getWriter().println("\t\t runnings:"+heart.runnings.toString());
								resp.getWriter().println("\t\t manual runnings:"+heart.manualRunnings.toString());
								resp.getWriter().println("\t\t debug runnings:"+heart.debugRunnings.toString());
							}
						}else if("queue".equals(op)){
							Queue<JobElement> queue=context.getQueue();
							Queue<JobElement> debugQueue=context.getDebugQueue();
							Queue<JobElement> manualQueue=context.getManualQueue();
							resp.getWriter().println("schedule jobs in queue:");
							for(JobElement jobId:queue){
								resp.getWriter().print(jobId.getJobID()+"\t");
							}
							resp.getWriter().println("manual jobs in queue:");
							for(JobElement jobId:manualQueue){
								resp.getWriter().print(jobId.getJobID()+"\t");
							}
							resp.getWriter().println("debug jobs in queue");
							for(JobElement jobId:debugQueue){
								resp.getWriter().print(jobId.getJobID()+"\t");
							}
						}else if("jobstatus".equals(op)){
							Dispatcher dispatcher=context.getDispatcher();
							if(dispatcher!=null){
								for(Controller c:dispatcher.getControllers()){
									resp.getWriter().println(c.toString());
								}
							}
						}else{
							resp.getWriter().println("<a href='dump.do?op=jobstatus'>Job调度状态</a>");
							resp.getWriter().println("<a href='dump.do?op=workers'>master-worker 状态</a>");
							resp.getWriter().println("<a href='dump.do?op=queue' >等待队列任务</a>");
						}
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.getWriter().close();
	}
}
