package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.HiveParser.nullCondition_return;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.GroupDescriptor;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobDescriptorOld;
import com.taobao.zeus.model.JobDescriptorOld.JobRunTypeOld;
import com.taobao.zeus.model.JobDescriptorOld.JobScheduleTypeOld;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.processer.DownloadProcesser;
import com.taobao.zeus.model.processer.Processer;
import com.taobao.zeus.store.GroupBeanOld;
import com.taobao.zeus.store.GroupManagerOld;
import com.taobao.zeus.store.GroupManagerToolOld;
import com.taobao.zeus.store.JobBeanOld;
import com.taobao.zeus.store.mysql.persistence.GroupPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistenceOld;
import com.taobao.zeus.store.mysql.persistence.Worker;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.store.mysql.tool.GroupValidate;
import com.taobao.zeus.store.mysql.tool.JobValidateOld;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvertOld;
import com.taobao.zeus.util.Tuple;

@SuppressWarnings("unchecked")
public class MysqlGroupManagerOld extends HibernateDaoSupport implements
		GroupManagerOld {
	@Override
	public void deleteGroup(String user, String groupId) throws ZeusException {
		GroupBeanOld group = getDownstreamGroupBean(groupId);
		if (group.isDirectory()) {
//			if (!group.getChildrenGroupBeans().isEmpty()) {
//				throw new ZeusException("该组下不为空，无法删除");
//			}
			boolean candelete = true;
			for (GroupBeanOld child : group.getChildrenGroupBeans()) {
				if (child.isExisted()) {
					candelete = false;
					break;
				}
			}
			if (!candelete) {
				throw new ZeusException("该组下不为空，无法删除");
			}
		} else {
			if (!group.getJobBeans().isEmpty()) {
				throw new ZeusException("该组下不为空，无法删除");
			}
		}
		GroupPersistence object = (GroupPersistence)getHibernateTemplate().get(GroupPersistence.class,
				Integer.valueOf(groupId));
		object.setExisted(0);
		getHibernateTemplate().update(object);
	}

	@Override
	public void deleteJob(String user, String jobId) throws ZeusException {
		GroupBeanOld root = getGlobeGroupBean();
		JobBeanOld job = root.getAllSubJobBeans().get(jobId);
		if (!job.getDepender().isEmpty()) {
			List<String> deps = new ArrayList<String>();
			for (JobBeanOld jb : job.getDepender()) {
				deps.add(jb.getJobDescriptor().getId());
			}
			throw new ZeusException("该Job正在被其他Job" + deps.toString()
					+ "依赖，无法删除");
		}
		getHibernateTemplate().delete(
				getHibernateTemplate().get(JobPersistenceOld.class,
						Long.valueOf(jobId)));
	}

	@Override
	public GroupBeanOld getDownstreamGroupBean(String groupId) {
		GroupDescriptor group = getGroupDescriptor(groupId);
		GroupBeanOld result = new GroupBeanOld(group);
		return getDownstreamGroupBean(result);
	}

	@Override
	public GroupBeanOld getDownstreamGroupBean(GroupBeanOld parent) {
		if (parent.isDirectory()) {
			List<GroupDescriptor> children = getChildrenGroup(parent
					.getGroupDescriptor().getId());
			for (GroupDescriptor child : children) {
				GroupBeanOld childBean = new GroupBeanOld(child);
				getDownstreamGroupBean(childBean);
				childBean.setParentGroupBean(parent);
				parent.getChildrenGroupBeans().add(childBean);
			}
		} else {
			List<Tuple<JobDescriptorOld, JobStatus>> jobs = getChildrenJob(parent
					.getGroupDescriptor().getId());
			for (Tuple<JobDescriptorOld, JobStatus> tuple : jobs) {
				JobBeanOld JobBeanOld = new JobBeanOld(tuple.getX(), tuple.getY());
				JobBeanOld.setGroupBean(parent);
				parent.getJobBeans().put(tuple.getX().getId(), JobBeanOld);
			}
		}

		return parent;
	}

	@Override
	public GroupBeanOld getGlobeGroupBean() {
		return GroupManagerToolOld.buildGlobeGroupBean(this);
	}

	/**
	 * 获取叶子组下所有的Job
	 * 
	 * @param groupId
	 * @return
	 */
	@Override
	public List<Tuple<JobDescriptorOld, JobStatus>> getChildrenJob(String groupId) {
		List<JobPersistenceOld> list = getHibernateTemplate().find(
				"from com.taobao.zeus.store.mysql.persistence.JobPersistenceOld where groupId="
						+ groupId);
		List<Tuple<JobDescriptorOld, JobStatus>> result = new ArrayList<Tuple<JobDescriptorOld, JobStatus>>();
		if (list != null) {
			for (JobPersistenceOld j : list) {
				result.add(PersistenceAndBeanConvertOld.convert(j));
			}
		}
		return result;
	}

	/**
	 * 获取组的下级组列表
	 * 
	 * @param groupId
	 * @return
	 */
	@Override
	public List<GroupDescriptor> getChildrenGroup(String groupId) {
		List<GroupPersistence> list = getHibernateTemplate().find(
				"from com.taobao.zeus.store.mysql.persistence.GroupPersistence where parent="
						+ groupId);
		List<GroupDescriptor> result = new ArrayList<GroupDescriptor>();
		if (list != null) {
			for (GroupPersistence p : list) {
				result.add(PersistenceAndBeanConvertOld.convert(p));
			}
		}
		return result;
	}

	@Override
	public GroupDescriptor getGroupDescriptor(String groupId) {
		GroupPersistence persist = (GroupPersistence) getHibernateTemplate()
				.get(GroupPersistence.class, Integer.valueOf(groupId));
		if (persist != null) {
			return PersistenceAndBeanConvertOld.convert(persist);
		}
		return null;
	}

	@Override
	public Tuple<JobDescriptorOld, JobStatus> getJobDescriptor(String jobId) {
		JobPersistenceOld persist = getJobPersistence(jobId);
		if (persist == null) {
			return null;
		}
		Tuple<JobDescriptorOld, JobStatus> t = PersistenceAndBeanConvertOld
				.convert(persist);
		JobDescriptorOld jd = t.getX();
		// 如果是周期任务，并且依赖不为空，则需要封装周期任务的依赖
		if (jd.getScheduleType() == JobScheduleTypeOld.CyleJob
				&& jd.getDependencies() != null) {
			JobPersistenceOld jp = null;
			for (String jobID : jd.getDependencies()) {
				if (StringUtils.isNotEmpty(jobID)) {
					jp = getJobPersistence(jobID);
					if(jp!=null){
						jd.getDepdCycleJob().put(jobID, jp.getCycle());
					}
				}
			}

		}
		return t;
	}

	private JobPersistenceOld getJobPersistence(String jobId) {
		JobPersistenceOld persist = (JobPersistenceOld) getHibernateTemplate().get(
				JobPersistenceOld.class, Long.valueOf(jobId));
		if (persist == null) {
			return null;
		}
		return persist;
	}

	@Override
	public String getRootGroupId() {
		return (String) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery("from com.taobao.zeus.store.mysql.persistence.GroupPersistence g order by g.id asc");
				query.setMaxResults(1);
				List<GroupPersistence> list = query.list();
				if (list == null || list.size() == 0) {
					GroupPersistence persist = new GroupPersistence();
					persist.setName("众神之神");
					persist.setOwner(ZeusUser.ADMIN.getUid());
					persist.setDirectory(0);
					session.save(persist);
					if (persist.getId() == null) {
						return null;
					}
					return String.valueOf(persist.getId());
				}
				return String.valueOf(list.get(0).getId());
			}
		});
	}

	@Override
	public GroupBeanOld getUpstreamGroupBean(String groupId) {
		return GroupManagerToolOld.getUpstreamGroupBean(groupId, this);
	}

	@Override
	public JobBeanOld getUpstreamJobBean(String jobId) {
		return GroupManagerToolOld.getUpstreamJobBean(jobId, this);
	}

	@Override
	public void updateGroup(String user, GroupDescriptor group)
			throws ZeusException {
		GroupPersistence old = (GroupPersistence) getHibernateTemplate().get(
				GroupPersistence.class, Integer.valueOf(group.getId()));
		updateGroup(user, group, old.getOwner(), old.getParent() == null ? null
				: old.getParent().toString());
	}

	public void updateGroup(String user, GroupDescriptor group, String owner,
			String parent) throws ZeusException {

		GroupPersistence old = (GroupPersistence) getHibernateTemplate().get(
				GroupPersistence.class, Integer.valueOf(group.getId()));

		GroupPersistence persist = PersistenceAndBeanConvertOld.convert(group);

		persist.setOwner(owner);
		if (parent != null) {
			persist.setParent(Integer.valueOf(parent));
		}

		// 以下属性不允许修改，强制采用老的数据
		persist.setDirectory(old.getDirectory());
		persist.setGmtCreate(old.getGmtCreate());
		persist.setGmtModified(new Date());
		persist.setExisted(old.getExisted());

		getHibernateTemplate().update(persist);
	}

	@Override
	public void updateJob(String user, JobDescriptorOld job) throws ZeusException {
		JobPersistenceOld orgPersist = (JobPersistenceOld) getHibernateTemplate()
				.get(JobPersistenceOld.class, Long.valueOf(job.getId()));
		updateJob(user, job, orgPersist.getOwner(), orgPersist.getGroupId()
				.toString());
	}

	public void updateJob(String user, JobDescriptorOld job, String owner,
			String groupId) throws ZeusException {
		JobPersistenceOld orgPersist = (JobPersistenceOld) getHibernateTemplate()
				.get(JobPersistenceOld.class, Long.valueOf(job.getId()));
		if (job.getScheduleType() == JobScheduleTypeOld.Independent) {
			job.setDependencies(new ArrayList<String>());
		} else if (job.getScheduleType() == JobScheduleTypeOld.Dependent) {
			job.setCronExpression("");
		}
		job.setOwner(owner);
		job.setGroupId(groupId);
		// 以下属性不允许修改，强制采用老的数据
		JobPersistenceOld persist = PersistenceAndBeanConvertOld.convert(job);
		persist.setGmtCreate(orgPersist.getGmtCreate());
		persist.setGmtModified(new Date());
		persist.setRunType(orgPersist.getRunType());
		persist.setStatus(orgPersist.getStatus());
		persist.setReadyDependency(orgPersist.getReadyDependency());
		persist.setHost(job.getHost());
		persist.setHostGroupId(Integer.valueOf(job.getHostGroupId()));
		// 如果是用户从界面上更新，开始时间、统计周期等均为空，用原来的值
		if (job.getStartTime() == null || "".equals(job.getStartTime())) {
			persist.setStartTime(orgPersist.getStartTime());
		}
		if (job.getStartTimestamp() == 0) {
			persist.setStartTimestamp(orgPersist.getStartTimestamp());
		}
		if (job.getStatisStartTime() == null
				|| "".equals(job.getStatisStartTime())) {
			persist.setStatisStartTime(orgPersist.getStatisStartTime());
		}
		if (job.getStatisEndTime() == null || "".equals(job.getStatisEndTime())) {
			persist.setStatisEndTime(orgPersist.getStatisEndTime());
		}

		// 如果是周期任务，则许检查依赖周期是否正确
		if (JobScheduleTypeOld.CyleJob.equals(job.getScheduleType())
				&& job.getDependencies() != null
				&& job.getDependencies().size() != 0) {
			List<JobDescriptorOld> list = this.getJobDescriptors(job
					.getDependencies());
			jobValidateOld.checkCycleJob(job, list);
		}

		if (jobValidateOld.valide(job)) {
			getHibernateTemplate().update(persist);
		}

	}

	@Autowired
	private JobValidateOld jobValidateOld;

	@Override
	public GroupDescriptor createGroup(String user, String groupName,
			String parentGroup, boolean isDirectory) throws ZeusException {
		if (parentGroup == null) {
			throw new ZeusException("parent group may not be null");
		}
		GroupDescriptor group = new GroupDescriptor();
		group.setOwner(user);
		group.setName(groupName);
		group.setParent(parentGroup);
		group.setDirectory(isDirectory);

		GroupValidate.valide(group);

		GroupPersistence persist = PersistenceAndBeanConvertOld.convert(group);
		persist.setGmtCreate(new Date());
		persist.setGmtModified(new Date());
		persist.setExisted(1);
		getHibernateTemplate().save(persist);
		return PersistenceAndBeanConvertOld.convert(persist);
	}

	@Override
	public JobDescriptorOld createJob(String user, String jobName,
			String parentGroup, JobRunTypeOld jobType) throws ZeusException {
		GroupDescriptor parent = getGroupDescriptor(parentGroup);
		if (parent.isDirectory()) {
			throw new ZeusException("目录组下不得创建Job");
		}
		JobDescriptorOld job = new JobDescriptorOld();
		job.setOwner(user);
		job.setName(jobName);
		job.setGroupId(parentGroup);
		job.setJobType(jobType);
		job.setPreProcessers(Arrays.asList((Processer) new DownloadProcesser()));
		JobPersistenceOld persist = PersistenceAndBeanConvertOld.convert(job);
		persist.setGmtCreate(new Date());
		persist.setGmtModified(new Date());
		getHibernateTemplate().save(persist);
		return PersistenceAndBeanConvertOld.convert(persist).getX();
	}

	@Override
	public Map<String, Tuple<JobDescriptorOld, JobStatus>> getJobDescriptor(
			final Collection<String> jobIds) {
		List<Tuple<JobDescriptorOld, JobStatus>> list = (List<Tuple<JobDescriptorOld, JobStatus>>) getHibernateTemplate()
				.execute(new HibernateCallback() {

					@Override
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						if (jobIds.isEmpty()) {
							return Collections.emptyList();
						}
						List<Long> ids = new ArrayList<Long>();
						for (String i : jobIds) {
							ids.add(Long.valueOf(i));
						}
						Query query = session
								.createQuery("from com.taobao.zeus.store.mysql.persistence.JobPersistenceOld where id in (:list)");
						query.setParameterList("list", ids);
						List<JobPersistenceOld> list = query.list();
						List<Tuple<JobDescriptorOld, JobStatus>> result = new ArrayList<Tuple<JobDescriptorOld, JobStatus>>();
						if (list != null && !list.isEmpty()) {
							for (JobPersistenceOld persist : list) {
								result.add(PersistenceAndBeanConvertOld
										.convert(persist));
							}
						}
						return result;
					}
				});

		Map<String, Tuple<JobDescriptorOld, JobStatus>> map = new HashMap<String, Tuple<JobDescriptorOld, JobStatus>>();
		for (Tuple<JobDescriptorOld, JobStatus> jd : list) {
			map.put(jd.getX().getId(), jd);
		}
		return map;
	}

	public List<JobDescriptorOld> getJobDescriptors(final Collection<String> jobIds) {
		List<JobDescriptorOld> list = (List<JobDescriptorOld>) getHibernateTemplate()
				.execute(new HibernateCallback() {
					@Override
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						if (jobIds.isEmpty()) {
							return Collections.emptyList();
						}
						List<Long> ids = new ArrayList<Long>();
						for (String i : jobIds) {
							if (StringUtils.isNotEmpty(i)) {
								ids.add(Long.valueOf(i));
							}
						}
						if (ids.isEmpty()) {
							return Collections.emptyList();
						}
						Query query = session
								.createQuery("from com.taobao.zeus.store.mysql.persistence.JobPersistenceOld where id in (:list)");
						query.setParameterList("list", ids);
						List<JobPersistenceOld> list = query.list();
						List<JobDescriptorOld> result = new ArrayList<JobDescriptorOld>();
						if (list != null && !list.isEmpty()) {
							for (JobPersistenceOld persist : list) {
								result.add(PersistenceAndBeanConvertOld.convert(
										persist).getX());
							}
						}
						return result;
					}
				});
		return list;
	}

	@Override
	public void updateJobStatus(JobStatus jobStatus) {
		JobPersistenceOld persistence = getJobPersistence(jobStatus.getJobId());
		persistence.setGmtModified(new Date());

		// 只修改状态 和 依赖 2个字段
		JobPersistenceOld temp = PersistenceAndBeanConvertOld.convert(jobStatus);
		persistence.setStatus(temp.getStatus());
		persistence.setReadyDependency(temp.getReadyDependency());
		persistence.setHistoryId(temp.getHistoryId());

		getHibernateTemplate().update(persistence);
	}

	@Override
	public JobStatus getJobStatus(String jobId) {
		Tuple<JobDescriptorOld, JobStatus> tuple = getJobDescriptor(jobId);
		if (tuple == null) {
			return null;
		}
		return tuple.getY();
	}

	@Override
	public void grantGroupOwner(String granter, String uid, String groupId)
			throws ZeusException {
		GroupDescriptor gd = getGroupDescriptor(groupId);
		if (gd != null) {
			updateGroup(granter, gd, uid, gd.getParent());
		}
	}

	@Override
	public void grantJobOwner(String granter, String uid, String jobId)
			throws ZeusException {
		Tuple<JobDescriptorOld, JobStatus> job = getJobDescriptor(jobId);
		if (job != null) {
			job.getX().setOwner(uid);
			updateJob(granter, job.getX(), uid, job.getX().getGroupId());
		}
	}

	@Override
	public void moveJob(String uid, String jobId, String groupId)
			throws ZeusException {
		JobDescriptorOld jd = getJobDescriptor(jobId).getX();
		GroupDescriptor gd = getGroupDescriptor(groupId);
		if (gd.isDirectory()) {
			throw new ZeusException("非法操作");
		}
		updateJob(uid, jd, jd.getOwner(), groupId);
	}

	@Override
	public void moveGroup(String uid, String groupId, String newParentGroupId)
			throws ZeusException {
		GroupDescriptor gd = getGroupDescriptor(groupId);
		GroupDescriptor parent = getGroupDescriptor(newParentGroupId);
		if (!parent.isDirectory()) {
			throw new ZeusException("非法操作");
		}
		updateGroup(uid, gd, gd.getOwner(), newParentGroupId);
	}

	@Override
	public List<String> getHosts() throws ZeusException {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select host from com.taobao.zeus.store.mysql.persistence.Worker");
						return query.list();
					}
				});
	}

	@Override
	public void replaceWorker(Worker worker) throws ZeusException {
		try {
			getHibernateTemplate().saveOrUpdate(worker);
		} catch (DataAccessException e) {
			throw new ZeusException(e);
		}

	}

	@Override
	public void removeWorker(String host) throws ZeusException {
		try {
			getHibernateTemplate().delete(
					getHibernateTemplate().get(Worker.class, host));
		} catch (DataAccessException e) {
			throw new ZeusException(e);
		}

	}
	
	/**
	 * 获取All Jobs
	 * 
	 * @param groupId
	 * @return
	 */
	@Override
	public List<JobPersistenceOld> getAllJobs() {
		List<JobPersistenceOld> list = getHibernateTemplate().find(
				"from com.taobao.zeus.store.mysql.persistence.JobPersistenceOld ");
		return list;
	}
	
	@Override
	public List<String> getAllDependencied(String jobID) {
		List<JobPersistenceOld> jobs = getAllJobs();
		if( jobs == null || jobs.size() == 0) return null;
		Map<String, List<String>> allJobDependencied = new HashMap<String, List<String>>();
		for(JobPersistenceOld job : jobs){
			JobDescriptorOld jobd = PersistenceAndBeanConvertOld.convert(job).getX();
			if( jobd != null && jobd.hasDependencies()){
				List<String> deps = jobd.getDependencies();
				for(String dep : deps){
					List<String> depds = allJobDependencied.get(dep);
					if(depds == null){
						depds = new ArrayList<String>();
					}
					depds.add(job.getId().toString());
					allJobDependencied.put(dep, depds);
				}
			}
		}
		
		List<String> dependencied = new ArrayList<String>();
		Set<String> visited = new HashSet<String>();
		Queue<String> idQueue = new LinkedList<String>();
		idQueue.offer(jobID);
		visited.add(jobID);
		while (!idQueue.isEmpty()) {
			String id = idQueue.poll();
			List<String> depdList = allJobDependencied.get(id);
			if(depdList !=null && depdList.size() != 0){
				for (String depd : depdList) {
					if (!visited.contains(depd)) {
						visited.add(depd);
						idQueue.offer(depd);
						dependencied.add(depd);
					}
				}
			}
		}
		return dependencied;
	}

	@Override
	public List<String> getAllDependencies(String jobID) {
		JobDescriptorOld job = getJobDescriptor(jobID).getX();
		if(job == null || !job.hasDependencies()) return null;
		List<String> dependencies = new ArrayList<String>();
		Set<String> visited = new HashSet<String>();
		Queue<String> idQueue = new LinkedList<String>();
		idQueue.offer(jobID);
		visited.add(jobID);
		while (!idQueue.isEmpty()) {
			String id = idQueue.poll();
			JobDescriptorOld jb = getJobDescriptor(id).getX();
			if (jb != null && jb.hasDependencies()) {
				List<String> deps = jb.getDependencies();
				if (deps != null && deps.size() != 0) {
					for (String dep : deps) {
						if (!visited.contains(dep)) {
							visited.add(dep);
							idQueue.offer(dep);
							dependencies.add(dep);
						}
					}
				}
			}
		}
		return dependencies;
	}
	
	@Override
	public void updateActionList(JobDescriptorOld job) {
		JobPersistenceOld persist = PersistenceAndBeanConvertOld.convert(job);
		Long jobId = persist.getId();
		String script = persist.getScript();
		String resources = persist.getResources();
		String configs = persist.getConfigs();
		String host = persist.getHost();
		Integer workGroupId = persist.getHostGroupId();
		logger.info("begin updateActionList.");
		HibernateTemplate template = getHibernateTemplate();
		List<JobPersistence> actionList = template.find("from com.taobao.zeus.store.mysql.persistence.JobPersistence where toJobId='"+ jobId +"' order by id desc");
		logger.info("finish query.");
		if (actionList != null && actionList.size() > 0 ){
			for(JobPersistence actionPer : actionList){
//				if(!"running".equalsIgnoreCase(actionPer.getStatus())){
					actionPer.setScript(script);
					actionPer.setResources(resources);
					actionPer.setConfigs(configs);
					actionPer.setHost(host);
					actionPer.setGmtModified(new Date());
					actionPer.setHostGroupId(workGroupId);
					template.saveOrUpdate(actionPer);
//				}
			}
			logger.info("finish update " + actionList.size() + ".");
		}
	}
}