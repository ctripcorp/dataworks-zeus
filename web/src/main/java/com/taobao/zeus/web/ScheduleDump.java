package com.taobao.zeus.web;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.zeus.model.HostGroupCache;
import com.taobao.zeus.mvc.Controller;
import com.taobao.zeus.mvc.Dispatcher;
import com.taobao.zeus.schedule.DistributeLocker;
import com.taobao.zeus.schedule.ZeusSchedule;
import com.taobao.zeus.schedule.mvc.JobController;
import com.taobao.zeus.schedule.mvc.event.Events;
import com.taobao.zeus.schedule.mvc.event.JobMaintenanceEvent;
import com.taobao.zeus.socket.master.JobElement;
import com.taobao.zeus.socket.master.MasterContext;
import com.taobao.zeus.socket.master.MasterWorkerHolder;
import com.taobao.zeus.socket.master.MasterWorkerHolder.HeartBeatInfo;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistenceOld;
import com.taobao.zeus.util.Environment;

/**
 * Dump调度系统内的Job状态，用来调试排查问题
 * 
 * @author zhoufang
 * 
 */
public class ScheduleDump extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private DistributeLocker locker;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		locker = (DistributeLocker) context.getBean("distributeLocker");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		try {
			if (locker != null) {
				Field zeusScheduleField = locker.getClass().getDeclaredField(
						"zeusSchedule");
				zeusScheduleField.setAccessible(true);
				ZeusSchedule zeusSchedule = (ZeusSchedule) zeusScheduleField
						.get(locker);
				if (zeusSchedule != null) {
					Field masterContextField = zeusSchedule.getClass()
							.getDeclaredField("context");
					masterContextField.setAccessible(true);
					MasterContext context = (MasterContext) masterContextField
							.get(zeusSchedule);
					if (context != null) {
						String op = req.getParameter("op");
						if ("workers".equals(op)) {
							Map<Channel, MasterWorkerHolder> workers = context
									.getWorkers();
							SimpleDateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							StringBuilder builder = new StringBuilder();
							builder.append("<table border=\"1\">");
							for (Channel channel : workers.keySet()) {
								MasterWorkerHolder holder = workers.get(channel);
								Set<String> runnings = holder.getRunnings().keySet();
								Set<String> manualRunnings = holder.getManualRunnings().keySet();
								Set<String> debugRunnings = holder.getDebugRunnings().keySet();
								HeartBeatInfo heart = holder.getHeart();
								builder.append("<tr>");
								builder.append("<td>");
								builder.append(channel.getRemoteAddress()+ ":");
								builder.append("<br>" + "\t runnings:"+ runnings.toString());
								builder.append("<br>" + "\t manual runnings:"+ manualRunnings.toString());
								builder.append("<br>" + "\t debug runnings:"+ debugRunnings.toString());
								builder.append("<br>" + "\t heart beat: ");
								if (heart != null) {
									builder.append("<br>"+ "\t\t last heartbeat:"+ format.format(heart.timestamp));
									if (heart.memRate < Environment.getMaxMemRate()) {
										builder.append("<br>" + "\t\t mem use rate:"+ heart.memRate);
									}else {
										builder.append("<br>" + "\t\t <font color=\"red\"> mem use rate:"+ heart.memRate+"</font>");
									}
									if (heart.cpuLoadPerCore < Environment.getMaxCpuLoadPerCore()) {
										builder.append("<br>" + "\t\t  cpu load per core:"+ heart.cpuLoadPerCore);
									}else {
										builder.append("<br>" + "\t\t <font color=\"red\"> cpu load per core:"+ heart.cpuLoadPerCore+"</font>");
									}
									builder.append("<br>"+ "\t\t runnings:"+ heart.runnings.toString());
									builder.append("<br>"+ "\t\t manual runnings:"+ heart.manualRunnings.toString());
									builder.append("<br>"+ "\t\t debug runnings:"+ heart.debugRunnings.toString());
								}
								builder.append("</td>");
								builder.append("</tr>");
							}
							builder.append("<tr>");
							builder.append("<td>");
							builder.append("\t\t Max Mem use Rate:"+ Environment.getMaxMemRate());
							builder.append("<br>" + "\t\t Max Cpu Load Per Core:"+ Environment.getMaxCpuLoadPerCore());
							builder.append("</td>");
							builder.append("</tr>");
							builder.append("</table>");
							resp.getWriter().println(builder.toString());
						} else if ("queue".equals(op)) {
							Queue<JobElement> queue = context.getQueue();
							Queue<JobElement> exceptionQueue = context.getExceptionQueue();
							Queue<JobElement> debugQueue = context.getDebugQueue();
							Queue<JobElement> manualQueue = context.getManualQueue();
							resp.getWriter().println("<br>" + "schedule jobs in queue:");
							for (JobElement jobId : queue) {
								resp.getWriter().print(jobId.getJobID() + "\t");
							}
							resp.getWriter().println("<br>" + "exception jobs in queue:");
							for (JobElement jobId : exceptionQueue) {
								resp.getWriter().print(jobId.getJobID() + "\t");
							}
							resp.getWriter().println("<br>" + "manual jobs in queue:");
							for (JobElement jobId : manualQueue) {
								resp.getWriter().print(jobId.getJobID() + "\t");
							}
							resp.getWriter().println("<br>" + "debug jobs in queue:");
							for (JobElement jobId : debugQueue) {
								resp.getWriter().print(jobId.getJobID() + "\t");
							}
							
						} else if ("jobstatus".equals(op)) {
							Dispatcher dispatcher = context.getDispatcher();
							if (dispatcher != null) {
								for (Controller c : dispatcher.getControllers()) {
									resp.getWriter().println(
											"<br>" + c.toString());
								}
							}
						}/*
						 * else if ("clearschedule".equals(op)) { Date now = new
						 * Date(); SimpleDateFormat df = new SimpleDateFormat(
						 * "yyyyMMddHHmmss"); String currentDateStr =
						 * df.format(now) + "0000"; Dispatcher dispatcher =
						 * context.getDispatcher(); if (dispatcher != null) {
						 * List<Controller> controllers = dispatcher
						 * .getControllers(); if (controllers != null &&
						 * controllers.size() > 0) { for (Controller c :
						 * controllers) { JobController jobc = (JobController)
						 * c; String jobId = jobc.getJobId(); if
						 * (Long.parseLong(jobId) < Long
						 * .parseLong(currentDateStr)) {
						 * context.getScheduler().deleteJob( jobId, "zeus"); } }
						 * } } resp.getWriter().println("清理完毕！");
						 * 
						 * }
						 */else if ("action".equals(op)) {
							Date now = new Date();
							SimpleDateFormat df2 = new SimpleDateFormat(
									"yyyy-MM-dd");
							SimpleDateFormat df3 = new SimpleDateFormat(
									"yyyyMMddHHmmss");
							String currentDateStr = df3.format(now) + "0000";
							List<JobPersistenceOld> jobDetails = context
									.getGroupManagerOld().getAllJobs();
							Map<Long, JobPersistence> actionDetails = new HashMap<Long, JobPersistence>();
							context.getMaster().runScheduleJobToAction(
									jobDetails, now, df2, actionDetails,
									currentDateStr);
							context.getMaster().runDependencesJobToAction(
									jobDetails, actionDetails, currentDateStr,
									0);

							Dispatcher dispatcher = context.getDispatcher();
							if (dispatcher != null) {
								// 增加controller，并修改event
								if (actionDetails.size() > 0) {
									List<Long> rollBackActionId = new ArrayList<Long>();
									for (Long id : actionDetails.keySet()) {
										dispatcher
												.addController(new JobController(
														context, context
																.getMaster(),
														id.toString()));
										if (id > Long.parseLong(currentDateStr)) {
											context.getDispatcher()
													.forwardEvent(
															new JobMaintenanceEvent(
																	Events.UpdateJob,
																	id.toString()));
										} else if (id < (Long
												.parseLong(currentDateStr) - 15000000)) {
											int loopCount = 0;
											context.getMaster()
													.rollBackLostJob(id,
															actionDetails,
															loopCount,
															rollBackActionId);

										}
									}
								}

								// 清理schedule
								List<Controller> controllers = dispatcher
										.getControllers();
								if (controllers != null
										&& controllers.size() > 0) {
									Iterator<Controller> itController = controllers
											.iterator();
									while (itController.hasNext()) {
										JobController jobc = (JobController) itController
												.next();
										String jobId = jobc.getJobId();
										if (Long.parseLong(jobId) < (Long
												.parseLong(currentDateStr) - 15000000)) {
											try {
												context.getScheduler()
														.deleteJob(jobId,
																"zeus");
											} catch (SchedulerException e) {
												e.printStackTrace();
											}
										} else if (Long.parseLong(jobId) >= Long
												.parseLong(currentDateStr)) {
											try {
												if (!actionDetails
														.containsKey(Long
																.valueOf(jobId))) {
													context.getScheduler()
															.deleteJob(jobId,
																	"zeus");
													context.getGroupManager()
															.removeJob(
																	Long.valueOf(jobId));
													itController.remove();
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							}
							resp.getWriter().println("Action生成完毕！");
						} else if ("hostgroup".equals(op)) {
							List<HostGroupCache> allHostGroupInfomations = context
									.getHostGroupCache();
							StringBuilder builder = new StringBuilder();
							builder.append("<h3>host组信息：</h3>");
							builder.append("<table border=\"1\">");
							builder.append("<tr>");
							builder.append("<th>组id</th>");
							builder.append("<th>名称</th>");
							builder.append("<th>描述</th>");
							builder.append("<th>host</th>");
							builder.append("</tr>");
							for (HostGroupCache info : allHostGroupInfomations) {
								builder.append("<tr>");
								builder.append("<td>" + info.getId() + "</td>");
								builder.append("<td>" + info.getName()
										+ "</td>");
								builder.append("<td>" + info.getDescription()
										+ "</td>");
								builder.append("<td>");
								for (String hosts : info.getHosts()) {
									builder.append(hosts + "<br/>");
								}
								builder.append("</td>");
								builder.append("</tr>");
							}
							builder.append("</table>");
							builder.append("<br><a href='dump.do?op=refreshhostgroup'>刷新</a>");
							resp.getWriter().println(builder.toString());
						} else if ("refreshhostgroup".equals(op)) {
							context.refreshHostGroupCache();
							resp.sendRedirect("dump.do?op=hostgroup");
						}
						else {
							resp.getWriter().println("<a href='dump.do?op=jobstatus'>查看Job调度状态</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=workers'>查看master-worker 状态</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=queue'>等待队列任务</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=action'>生成Action版本</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=hostgroup'>查看host分组信息</a>&nbsp;&nbsp;&nbsp;&nbsp;");
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.getWriter().close();
		// req.getRequestDispatcher("/login.jsp").forward(req, resp);
		// resp.sendRedirect("/login.jsp");
	}
}
