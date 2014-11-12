package com.taobao.zeus.util;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.mortbay.log.Log;
import org.springframework.context.ApplicationContext;

import com.taobao.zeus.jobs.Job;
import com.taobao.zeus.jobs.JobContext;
import com.taobao.zeus.jobs.RenderHierarchyProperties;
import com.taobao.zeus.jobs.WithProcesserJob;
import com.taobao.zeus.jobs.sub.HadoopShellJob;
import com.taobao.zeus.jobs.sub.HiveJob;
import com.taobao.zeus.jobs.sub.MapReduceJob;
import com.taobao.zeus.jobs.sub.tool.DownloadJob;
import com.taobao.zeus.jobs.sub.tool.HiveProcesserJob;
import com.taobao.zeus.jobs.sub.tool.MailJob;
import com.taobao.zeus.jobs.sub.tool.OutputCheckJob;
import com.taobao.zeus.jobs.sub.tool.OutputCleanJob;
import com.taobao.zeus.jobs.sub.tool.WangWangJob;
import com.taobao.zeus.jobs.sub.tool.ZooKeeperJob;
import com.taobao.zeus.model.DebugHistory;
import com.taobao.zeus.model.FileDescriptor;
import com.taobao.zeus.model.JobDescriptor.JobRunType;
import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.model.Profile;
import com.taobao.zeus.model.processer.DownloadProcesser;
import com.taobao.zeus.model.processer.HiveProcesser;
import com.taobao.zeus.model.processer.JobProcesser;
import com.taobao.zeus.model.processer.MailProcesser;
import com.taobao.zeus.model.processer.OutputCheckProcesser;
import com.taobao.zeus.model.processer.OutputCleanProcesser;
import com.taobao.zeus.model.processer.Processer;
import com.taobao.zeus.model.processer.WangWangProcesser;
import com.taobao.zeus.model.processer.ZooKeeperProcesser;
import com.taobao.zeus.store.FileManager;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.HierarchyProperties;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.ProfileManager;

public class JobUtils {

	public static final Pattern PT = Pattern
			.compile("download\\[(doc|hdfs|http)://.+]");

	public static Job createDebugJob(JobContext jobContext,
			DebugHistory history, String workDir,
			ApplicationContext applicationContext) {
		jobContext.setDebugHistory(history);
		jobContext.setWorkDir(workDir);
		HierarchyProperties hp = new HierarchyProperties(
				new HashMap<String, String>());
		String script = history.getScript();
		List<Map<String, String>> resources = new ArrayList<Map<String, String>>();
		// 处理脚本中的 资源引用 语句
		script = resolvScriptResource(resources, script, applicationContext);
		jobContext.setResources(resources);
		hp.setProperty(PropertyKeys.JOB_SCRIPT, script);
		FileManager fileManager = (FileManager) applicationContext
				.getBean("fileManager");
		ProfileManager profileManager = (ProfileManager) applicationContext
				.getBean("profileManager");
		String owner = fileManager.getFile(history.getFileId()).getOwner();
		Profile profile = profileManager.findByUid(owner);
		if (profile != null && profile.getHadoopConf() != null) {
			for (String key : profile.getHadoopConf().keySet()) {
				hp.setProperty(key, profile.getHadoopConf().get(key));
			}
		}
		jobContext.setProperties(new RenderHierarchyProperties(hp));
		hp.setProperty("hadoop.mapred.job.zeus_id",
				"zeus_debug_" + history.getId());
		List<Job> pres = new ArrayList<Job>(1);
		pres.add(new DownloadJob(jobContext));
		Job core = null;
		if (history.getJobRunType() == JobRunType.Hive) {
			core = new HiveJob(jobContext, applicationContext);
		} else if (history.getJobRunType() == JobRunType.Shell) {
			core = new HadoopShellJob(jobContext);
		}
		Job job = new WithProcesserJob(jobContext, pres, new ArrayList<Job>(),
				core, applicationContext);
		return job;
	}

	public static Job createJob(JobContext jobContext, JobBean jobBean,
			JobHistory history, String workDir,
			ApplicationContext applicationContext) {
		jobContext.setJobHistory(history);
		jobContext.setWorkDir(workDir);
		HierarchyProperties hp = jobBean.getHierarchyProperties();
		if (history.getProperties() != null
				&& !history.getProperties().isEmpty()) {
			history.getLog().appendZeus("This job hava instance configs:");
			for (String key : history.getProperties().keySet()) {
				hp.setProperty(key, history.getProperties().get(key));
				history.getLog().appendZeus(
						key + "=" + history.getProperties().get(key));
			}
		}
		jobContext.setProperties(new RenderHierarchyProperties(hp));
		List<Map<String, String>> resources = jobBean.getHierarchyResources();
		String script = jobBean.getJobDescriptor().getScript();
		///*************************update run date  2014-09-18**************
		String dateStr = history.getJobId().substring(0,14);
		System.out.println("Manual Job run date :"+dateStr);
		if(dateStr != null && dateStr.length() == 14){
			script = RenderHierarchyProperties.render(script, dateStr);
			System.out.println("Manual Job script :"+script);
		}		
		///*********************************************************
		// 处理脚本中的 资源引用 语句
		if (jobBean.getJobDescriptor().getJobType().equals(JobRunType.Shell)
				|| jobBean.getJobDescriptor().getJobType()
						.equals(JobRunType.Hive)) {
			script = resolvScriptResource(resources, script, applicationContext);
			jobBean.getJobDescriptor().setScript(script);
		}
		jobContext.setResources(resources);
		if(dateStr != null && dateStr.length() == 14){
			script = replace(jobContext.getProperties().getAllProperties(dateStr), script);
		}else{
			script = replace(jobContext.getProperties().getAllProperties(), script);
		}
		System.out.println("Manual Job last script :"+script);
		script=replaceScript(history,script);
		hp.setProperty(PropertyKeys.JOB_SCRIPT, script);

		/*// 添加宙斯标记属性，提供给云梯
		hp.setProperty("hadoop.mapred.job.zues_id",
				"zeus_job_" + history.getJobId() + "_" + history.getId());
*/
		// 前置处理Job创建
		List<Job> pres = parseJobs(jobContext, applicationContext, jobBean,
				jobBean.getJobDescriptor().getPreProcessers(), history, workDir);
		pres.add(0, new DownloadJob(jobContext));
		// 后置处理Job创建
		List<Job> posts = parseJobs(jobContext, applicationContext, jobBean,
				jobBean.getJobDescriptor().getPostProcessers(), history,
				workDir);
		posts.add(new ZooKeeperJob(jobContext, null, applicationContext));
		// 核心处理Job创建
		Job core = null;
		if (jobBean.getJobDescriptor().getJobType() == JobRunType.MapReduce) {
			core = new MapReduceJob(jobContext);
		} else if (jobBean.getJobDescriptor().getJobType() == JobRunType.Shell) {
			core = new HadoopShellJob(jobContext);
		} else if (jobBean.getJobDescriptor().getJobType() == JobRunType.Hive) {
			core = new HiveJob(jobContext, applicationContext);
		}

		Job job = new WithProcesserJob(jobContext, pres, posts, core,
				applicationContext);

		return job;
	}

	private static String replaceScript(JobHistory history, String script) {
		if (StringUtils.isEmpty(history.getStatisEndTime())
				|| StringUtils.isEmpty(history.getTimezone())) {
			return script;
		}
		script = script.replace("${j_set}", history.getStatisEndTime());
		try {
			script = script.replace("${j_est}",DateUtil.string2Timestamp(history.getStatisEndTime(),
							history.getTimezone()) / 1000 + "");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return script;
	}

	private static String resolvScriptResource(
			List<Map<String, String>> resources, String script,
			ApplicationContext context) {
		Matcher m = PT.matcher(script);
		while (m.find()) {
			String s = m.group();
			s = s.substring(s.indexOf('[') + 1, s.indexOf(']'));
			String[] args = StringUtils.split(s, ' ');
			String uri = args[0];
			String name = "";
			String referScript = null;
			String path = uri.substring(uri.lastIndexOf('/') + 1);
			Map<String, String> map = new HashMap<String, String>(2);
			if (uri.startsWith("doc://")) {
				FileManager manager = (FileManager) context
						.getBean("fileManager");
				FileDescriptor fd = manager.getFile(path);
				name = fd.getName();
				// 把脚本放到map里，减少后面一次getFile调用
				referScript = fd.getContent();
			}
			if (args.length > 1) {
				name = "";
				for (int i = 1; i < args.length; i++) {
					if (i > 1) {
						name += "_";
					}
					name += args[i];
				}
			} else if (args.length == 1) {
				// 没有指定文件名
				if (uri.startsWith("hdfs://")) {
					if (uri.endsWith("/")) {
						continue;
					}
					name = path;
				}
			}
			boolean exist = false;
			for (Map<String, String> ent : resources) {
				if (ent.get("name").equals(name)) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				map.put("uri", uri);
				map.put("name", name);
				resources.add(map);
				// 把脚本放到map里，减少后面一次getFile调用
				if (uri.startsWith("doc://") && referScript != null) {
					map.put("zeus-doc-" + path,
							resolvScriptResource(resources, referScript,
									context));
				}
			}
		}
		script = m.replaceAll("");
		return script;
	}

	private static String replace(Map<String, String> map, String content) {
		if (content == null) {
			return null;
		}
		Map<String, String> newmap = new HashMap<String, String>();
		for (String key : map.keySet()) {
			if (map.get(key) != null) {
				newmap.put("${" + key + "}", map.get(key));
			}
		}
		for (String key : newmap.keySet()) {
			String old = "";
			do {
				old = content;
				content = content.replace(key, newmap.get(key));
			} while (!old.equals(content));
		}
		return content;
	}

	private static List<Job> parseJobs(JobContext jobContext,
			ApplicationContext applicationContext, JobBean jobBean,
			List<Processer> ps, JobHistory history, String workDir) {
		List<Job> jobs = new ArrayList<Job>();
		Map<String, String> map = jobContext.getProperties().getAllProperties();
		Map<String, String> newmap = new HashMap<String, String>();
		try {
			for (String key : map.keySet()) {
				String value = map.get(key);
				if (value != null) {
					if (StringUtils.isNotEmpty(history.getStatisEndTime())
							&& StringUtils.isNotEmpty(history.getTimezone())) {
						value = value.replace("${j_set}",
								history.getStatisEndTime());
						value = value.replace(
								"${j_est}",
								DateUtil.string2Timestamp(
										history.getStatisEndTime(),
										history.getTimezone())
										/ 1000 + "");
						map.put(key, value);
					}
					newmap.put("${" + key + "}", value);
				}
			}
		} catch (ParseException e) {
			Log.warn("parse job end time to timestamp failed", e);
		}
		for (Processer p : ps) {
			String config = p.getConfig();
			if (config != null && !"".equals(config.trim())) {
				for (String key : newmap.keySet()) {
					String old = "";
					do {
						old = config;
						String value = newmap.get(key).replace("\"", "\\\"");
						config = config.replace(key, value);
					} while (!old.equals(config));
				}
				p.parse(config);
			}
			if (p instanceof DownloadProcesser) {
				jobs.add(new DownloadJob(jobContext));
			} else if (p instanceof ZooKeeperProcesser) {
				ZooKeeperProcesser zkp = (ZooKeeperProcesser) p;
				if (!zkp.getUseDefault()) {
					jobs.add(new ZooKeeperJob(jobContext,
							(ZooKeeperProcesser) p, applicationContext));
				}
			} else if (p instanceof MailProcesser) {
				jobs.add(new MailJob(jobContext, (MailProcesser) p,
						applicationContext));
			} else if (p instanceof WangWangProcesser) {
				jobs.add(new WangWangJob(jobContext));
			} else if (p instanceof OutputCheckProcesser) {
				jobs.add(new OutputCheckJob(jobContext,
						(OutputCheckProcesser) p, applicationContext));
			} else if (p instanceof OutputCleanProcesser) {
				jobs.add(new OutputCleanJob(jobContext,
						(OutputCleanProcesser) p, applicationContext));
			} else if (p instanceof HiveProcesser) {
				jobs.add(new HiveProcesserJob(jobContext, (HiveProcesser) p,
						applicationContext));
			} else if (p instanceof JobProcesser) {
				Integer depth = (Integer) jobContext.getData("depth");
				if (depth == null) {
					depth = 0;
				}
				if (depth < 2) {// job 的递归深度控制，防止无限递归
					JobProcesser jobProcesser = (JobProcesser) p;
					GroupManager groupManager = (GroupManager) applicationContext
							.getBean("groupManager");
					JobBean jb = groupManager.getUpstreamJobBean(jobProcesser
							.getJobId());
					if (jb != null) {
						for (String key : jobProcesser.getKvConfig().keySet()) {
							if (jobProcesser.getKvConfig().get(key) != null) {
								jb.getJobDescriptor()
										.getProperties()
										.put(key,
												jobProcesser.getKvConfig().get(
														key));
							}
						}
						File direcotry = new File(workDir + File.separator
								+ "job-processer-" + jobProcesser.getJobId());
						if (!direcotry.exists()) {
							direcotry.mkdirs();
						}
						JobContext sub = new JobContext(jobContext.getRunType());
						sub.putData("depth", ++depth);
						Job job = createJob(sub, jb, history,
								direcotry.getAbsolutePath(), applicationContext);
						jobs.add(job);
					}
				} else {
					jobContext.getJobHistory().getLog()
							.appendZeus("递归的JobProcesser处理单元深度过大，停止递归");
				}
			}
		}
		return jobs;
	}
	
	public static String getHadoopCmd(Map<String,String> evenMap){
		StringBuilder cmd=new StringBuilder(64);
		String hadoopHome=evenMap.get("HADOOP_HOME");
		if(hadoopHome!=null){
			cmd.append(hadoopHome).append("/bin/");
		}
		cmd.append("hadoop");
		return cmd.toString();
	}
}
