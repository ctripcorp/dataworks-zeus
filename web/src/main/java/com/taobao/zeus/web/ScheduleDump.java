package com.taobao.zeus.web;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jboss.netty.channel.Channel;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.zeus.model.HostGroupCache;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.JobStatus.Status;
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
import com.taobao.zeus.store.mysql.MysqlGroupManager;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistenceBackup;
import com.taobao.zeus.store.mysql.persistence.JobPersistenceOld;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvert;
import com.taobao.zeus.util.Tuple;
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
								builder.append("<br>" + "runnings:"+ runnings.toString());
								builder.append("<br>" + "manual runnings:"+ manualRunnings.toString());
								builder.append("<br>" + "debug runnings:"+ debugRunnings.toString());
								builder.append("<br>" + "heart beat: ");
								if (heart != null) {
									Date now = new Date();
									if (heart.timestamp == null) {
										builder.append("<br>"+ "<font color=\"red\">last heartbeat: null</font>");
									}
									else if ((now.getTime() - heart.timestamp.getTime()) > 1000 * 60) {
										builder.append("<br>"+ "<font color=\"red\">last heartbeat:"+ format.format(heart.timestamp)+"</font>");
									}
									else {
										builder.append("<br>"+ "last heartbeat:"+ format.format(heart.timestamp));
									}
									if (heart.memRate < Environment.getMaxMemRate()) {
										builder.append("<br>" + "mem use rate:"+ heart.memRate);
									}else {
										builder.append("<br>" + "<font color=\"red\">mem use rate:"+ heart.memRate+"</font>");
									}
									if (heart.cpuLoadPerCore < Environment.getMaxCpuLoadPerCore()) {
										builder.append("<br>" + "cpu load per core:"+ heart.cpuLoadPerCore);
									}else {
										builder.append("<br>" + "<font color=\"red\">cpu load per core:"+ heart.cpuLoadPerCore+"</font>");
									}
									builder.append("<br>"+ "runnings:"+ heart.runnings.toString());
									builder.append("<br>"+ "manual runnings:"+ heart.manualRunnings.toString());
									builder.append("<br>"+ "debug runnings:"+ heart.debugRunnings.toString());
								}
								builder.append("</td>");
								builder.append("</tr>");
							}
							builder.append("<tr>");
							builder.append("<td>");
							builder.append("Max Mem use Rate:"+ Environment.getMaxMemRate());
							builder.append("<br>" + "Max Cpu Load Per Core:"+ Environment.getMaxCpuLoadPerCore());
							builder.append("<br>" + "Number of hosts:"+ workers.size());
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
						}else if("clearactions".equals(op)){
							int before7day = -7;
							int cnt = 0;
							int sum = 0;
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.DATE, before7day);
							Date date = cal.getTime();
							SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd000000");
							String dateStr = df.format(date) + "0000";
							Dispatcher dispatcher = context.getDispatcher();
							if (dispatcher != null) {
								List<Controller> controllers = dispatcher.getControllers();
								if (controllers != null&& controllers.size() > 0) {
									resp.getWriter().println("开始清理内存中controller id为" + dateStr + "以前的controller：");
									List<JobDescriptor> toBeTransferred = new ArrayList<JobDescriptor>();
									Iterator<Controller> iter = controllers.iterator();
									while (iter.hasNext()) {
										sum ++ ;
										JobController jobc = (JobController) iter.next();
										String jobId = jobc.getJobId();
										if (Long.parseLong(jobId) < Long.parseLong(dateStr)) {
											Tuple<JobDescriptor, JobStatus> tuple = context.getGroupManager().getJobDescriptor(jobId);
											JobStatus status = tuple.getY();
											if (!Status.RUNNING.equals(status.getStatus())) {
												toBeTransferred.add(tuple.getX());
												iter.remove();
												//resp.getWriter().println("<br>成功清理了id为" + jobId + "的controller");
												cnt++;
											}
										}
									}
									resp.getWriter().println("<br>内存中共"+ sum +"个controllers，清理了一周前" + cnt+ "个controllers");
									if (toBeTransferred != null && toBeTransferred.size() > 0) {
										int bakCount = 0;
										MysqlGroupManager manager = ( MysqlGroupManager ) context.getApplicationContext ().getBean( "groupManager" );
										HibernateTemplate template = manager.getHibernateTemplate();
										SessionFactory factory = template.getSessionFactory();
										Session session = factory.openSession();
										Transaction tx = null;
										tx = session.beginTransaction();
										try {
											//String sqlBak = "insert into JobPersistenceBackup (id,toJobId) select c.id, c.toJobId from JobPersistence c where c.id<"+dateStr+" and c.status<>'running'";
											//bakCount = session.createSQLQuery(sqlBak).executeUpdate();
											//String sqlDel = "delete JobPersistence where id<" + dateStr + " and status<>'running'";
											//delCount = session.createSQLQuery(sqlDel).executeUpdate();
											resp.getWriter().println("<br><br>开始备份action表");
											for (JobDescriptor job : toBeTransferred) {
												JobPersistence persist = PersistenceAndBeanConvert.convert(job);
												JobPersistenceBackup backup = new JobPersistenceBackup(persist);
												//resp.getWriter().println("<br>备份数据库中id为" + job.getId() + "的action");
												
												session.delete(persist);
												session.saveOrUpdate(backup);
												bakCount ++;
											}
											tx.commit();
											resp.getWriter().println("<br>完成数据库备份， 共备份" + bakCount + "条数据");
										} catch (RuntimeException e) {
											try {
												tx.rollback();
											} catch (Exception e2) {
												resp.getWriter().println(e2.getMessage());
											}
											resp.getWriter().println(e.getMessage());
										} finally {
											try {
												if (session != null) {
													session.close();
												}
												if (factory != null) {
													factory.close();
												}
											} catch (Exception e3) {
												resp.getWriter().print(e3.getMessage());
											}
										}
									}

								}
							}

						}
						else {
							resp.getWriter().println("<a href='dump.do?op=jobstatus'>查看Job调度状态</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=workers'>查看master-worker 状态</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=queue'>等待队列任务</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=action'>生成Action版本</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=hostgroup'>查看host分组信息</a>&nbsp;&nbsp;&nbsp;&nbsp;");
							resp.getWriter().println("<a href='dump.do?op=clearactions'>清理一周前的action</a>&nbsp;&nbsp;&nbsp;&nbsp;");
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
