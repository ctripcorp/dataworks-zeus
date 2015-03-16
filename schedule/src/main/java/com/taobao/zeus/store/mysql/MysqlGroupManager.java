package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.GroupDescriptor;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobDescriptor.JobRunType;
import com.taobao.zeus.model.JobDescriptor.JobScheduleType;
import com.taobao.zeus.model.JobDescriptorOld;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.processer.DownloadProcesser;
import com.taobao.zeus.model.processer.Processer;
import com.taobao.zeus.store.GroupBean;
import com.taobao.zeus.store.GroupBeanOld;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.GroupManagerTool;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.mysql.persistence.GroupPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistenceOld;
import com.taobao.zeus.store.mysql.persistence.Worker;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.store.mysql.tool.GroupValidate;
import com.taobao.zeus.store.mysql.tool.JobValidate;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvert;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvertOld;
import com.taobao.zeus.util.Tuple;

@SuppressWarnings("unchecked")
public class MysqlGroupManager extends HibernateDaoSupport implements
		GroupManager {
	@Override
	public void deleteGroup(String user, String groupId) throws ZeusException {
		GroupBean group = getDownstreamGroupBean(groupId);
		if (group.isDirectory()) {
//			if (!group.getChildrenGroupBeans().isEmpty()) {
//				throw new ZeusException("该组下不为空，无法删除");
//			}
			boolean candelete = true;
			for (GroupBean child : group.getChildrenGroupBeans()) {
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
		GroupBean root = getGlobeGroupBean();
		JobBean job = root.getAllSubJobBeans().get(jobId);
		if (!job.getDepender().isEmpty()) {
			List<String> deps = new ArrayList<String>();
			for (JobBean jb : job.getDepender()) {
				deps.add(jb.getJobDescriptor().getId());
			}
			throw new ZeusException("该Job正在被其他Job" + deps.toString()
					+ "依赖，无法删除");
		}
		getHibernateTemplate().delete(
				getHibernateTemplate().get(JobPersistence.class,
						Long.valueOf(jobId)));
	}

	@Override
	public GroupBean getDownstreamGroupBean(String groupId) {
		GroupDescriptor group = getGroupDescriptor(groupId);
		GroupBean result = new GroupBean(group);
		return getDownstreamGroupBean(result);
	}

	@Override
	public GroupBean getDownstreamGroupBean(GroupBean parent) {
		if (parent.isDirectory()) {
			List<GroupDescriptor> children = getChildrenGroup(parent
					.getGroupDescriptor().getId());
			for (GroupDescriptor child : children) {
				GroupBean childBean = new GroupBean(child);
				getDownstreamGroupBean(childBean);
				childBean.setParentGroupBean(parent);
				parent.getChildrenGroupBeans().add(childBean);
			}
		} else {
			List<Tuple<JobDescriptor, JobStatus>> jobs = getChildrenJob(parent
					.getGroupDescriptor().getId());
			for (Tuple<JobDescriptor, JobStatus> tuple : jobs) {
				JobBean jobBean = new JobBean(tuple.getX(), tuple.getY());
				jobBean.setGroupBean(parent);
				parent.getJobBeans().put(tuple.getX().getId(), jobBean);
			}
		}

		return parent;
	}

	@Override
	public GroupBean getGlobeGroupBean() {
		return GroupManagerTool.buildGlobeGroupBean(this);
	}

	/**
	 * 获取叶子组下所有的Job
	 * 
	 * @param groupId
	 * @return
	 */
	@Override
	public List<Tuple<JobDescriptor, JobStatus>> getChildrenJob(String groupId) {
		List<JobPersistence> list = getHibernateTemplate().find(
				"from com.taobao.zeus.store.mysql.persistence.JobPersistence where groupId="
						+ groupId);
		List<Tuple<JobDescriptor, JobStatus>> result = new ArrayList<Tuple<JobDescriptor, JobStatus>>();
		if (list != null) {
			for (JobPersistence j : list) {
				result.add(PersistenceAndBeanConvert.convert(j));
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
				result.add(PersistenceAndBeanConvert.convert(p));
			}
		}
		return result;
	}

	@Override
	public GroupDescriptor getGroupDescriptor(String groupId) {
		GroupPersistence persist = (GroupPersistence) getHibernateTemplate()
				.get(GroupPersistence.class, Integer.valueOf(groupId));
		if (persist != null) {
			return PersistenceAndBeanConvert.convert(persist);
		}
		return null;
	}

	@Override
	public Tuple<JobDescriptor, JobStatus> getJobDescriptor(String jobId) {
		JobPersistence persist = getJobPersistence(jobId);
		if (persist == null) {
			return null;
		}
		Tuple<JobDescriptor, JobStatus> t = PersistenceAndBeanConvert
				.convert(persist);
		JobDescriptor jd = t.getX();
		// 如果是周期任务，并且依赖不为空，则需要封装周期任务的依赖
		if (jd.getScheduleType() == JobScheduleType.CyleJob
				&& jd.getDependencies() != null) {
			JobPersistence jp = null;
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

	private JobPersistence getJobPersistence(String jobId) {
		JobPersistence persist = (JobPersistence) getHibernateTemplate().get(
				JobPersistence.class, Long.valueOf(jobId));
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
	public GroupBean getUpstreamGroupBean(String groupId) {
		return GroupManagerTool.getUpstreamGroupBean(groupId, this);
	}

	@Override
	public JobBean getUpstreamJobBean(String jobId) {
		return GroupManagerTool.getUpstreamJobBean(jobId, this);
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

		GroupPersistence persist = PersistenceAndBeanConvert.convert(group);

		persist.setOwner(owner);
		if (parent != null) {
			persist.setParent(Integer.valueOf(parent));
		}

		// 以下属性不允许修改，强制采用老的数据
		persist.setDirectory(old.getDirectory());
		persist.setGmtCreate(old.getGmtCreate());
		persist.setGmtModified(new Date());

		getHibernateTemplate().update(persist);
	}

	@Override
	public void updateJob(String user, JobDescriptor job) throws ZeusException {
		JobPersistence orgPersist = (JobPersistence) getHibernateTemplate()
				.get(JobPersistence.class, Long.valueOf(job.getId()));
		updateJob(user, job, orgPersist.getOwner(), orgPersist.getGroupId()
				.toString());
	}

	public void updateJob(String user, JobDescriptor job, String owner,
			String groupId) throws ZeusException {
		JobPersistence orgPersist = (JobPersistence) getHibernateTemplate()
				.get(JobPersistence.class, Long.valueOf(job.getId()));
		if (job.getScheduleType() == JobScheduleType.Independent) {
			job.setDependencies(new ArrayList<String>());
		} else if (job.getScheduleType() == JobScheduleType.Dependent) {
			job.setCronExpression("");
		}
		job.setOwner(owner);
		job.setGroupId(groupId);
		// 以下属性不允许修改，强制采用老的数据
		JobPersistence persist = PersistenceAndBeanConvert.convert(job);
		persist.setGmtCreate(orgPersist.getGmtCreate());
		persist.setGmtModified(new Date());
		persist.setRunType(orgPersist.getRunType());
		persist.setStatus(orgPersist.getStatus());
		persist.setReadyDependency(orgPersist.getReadyDependency());
		persist.setHost(job.getHost());
		persist.setWorkerGroupId(Integer.valueOf(job.getWorkerGroupId()));
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
		if (job.getScheduleType().equals(JobScheduleType.CyleJob)
				&& job.getDependencies() != null
				&& job.getDependencies().size() != 0) {
			List<JobDescriptor> list = this.getJobDescriptors(job
					.getDependencies());
			jobValidate.checkCycleJob(job, list);
		}

		if (jobValidate.valide(job)) {
			getHibernateTemplate().update(persist);
		}

	}

	@Autowired
	private JobValidate jobValidate;

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

		GroupPersistence persist = PersistenceAndBeanConvert.convert(group);
		persist.setGmtCreate(new Date());
		persist.setGmtModified(new Date());
		persist.setExisted(1);
		getHibernateTemplate().save(persist);
		return PersistenceAndBeanConvert.convert(persist);
	}

	@Override
	public JobDescriptor createJob(String user, String jobName,
			String parentGroup, JobRunType jobType) throws ZeusException {
		GroupDescriptor parent = getGroupDescriptor(parentGroup);
		if (parent.isDirectory()) {
			throw new ZeusException("目录组下不得创建Job");
		}
		JobDescriptor job = new JobDescriptor();
		job.setOwner(user);
		job.setName(jobName);
		job.setGroupId(parentGroup);
		job.setJobType(jobType);
		job.setPreProcessers(Arrays.asList((Processer) new DownloadProcesser()));
		JobPersistence persist = PersistenceAndBeanConvert.convert(job);
		persist.setGmtCreate(new Date());
		persist.setGmtModified(new Date());
		getHibernateTemplate().save(persist);
		return PersistenceAndBeanConvert.convert(persist).getX();
	}

	@Override
	public Map<String, Tuple<JobDescriptor, JobStatus>> getJobDescriptor(
			final Collection<String> jobIds) {
		List<Tuple<JobDescriptor, JobStatus>> list = (List<Tuple<JobDescriptor, JobStatus>>) getHibernateTemplate()
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
								.createQuery("from com.taobao.zeus.store.mysql.persistence.JobPersistence where id in (:list)");
						query.setParameterList("list", ids);
						List<JobPersistence> list = query.list();
						List<Tuple<JobDescriptor, JobStatus>> result = new ArrayList<Tuple<JobDescriptor, JobStatus>>();
						if (list != null && !list.isEmpty()) {
							for (JobPersistence persist : list) {
								result.add(PersistenceAndBeanConvert
										.convert(persist));
							}
						}
						return result;
					}
				});

		Map<String, Tuple<JobDescriptor, JobStatus>> map = new HashMap<String, Tuple<JobDescriptor, JobStatus>>();
		for (Tuple<JobDescriptor, JobStatus> jd : list) {
			map.put(jd.getX().getId(), jd);
		}
		return map;
	}

	public List<JobDescriptor> getJobDescriptors(final Collection<String> jobIds) {
		List<JobDescriptor> list = (List<JobDescriptor>) getHibernateTemplate()
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
								.createQuery("from com.taobao.zeus.store.mysql.persistence.JobPersistence where id in (:list)");
						query.setParameterList("list", ids);
						List<JobPersistence> list = query.list();
						List<JobDescriptor> result = new ArrayList<JobDescriptor>();
						if (list != null && !list.isEmpty()) {
							for (JobPersistence persist : list) {
								result.add(PersistenceAndBeanConvert.convert(
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
		JobPersistence persistence = getJobPersistence(jobStatus.getJobId());
		persistence.setGmtModified(new Date());

		// 只修改状态 和 依赖 2个字段
		JobPersistence temp = PersistenceAndBeanConvert.convert(jobStatus);
		persistence.setStatus(temp.getStatus());
		persistence.setReadyDependency(temp.getReadyDependency());
		persistence.setHistoryId(temp.getHistoryId());

		getHibernateTemplate().update(persistence);
	}

	@Override
	public JobStatus getJobStatus(String jobId) {
		Tuple<JobDescriptor, JobStatus> tuple = getJobDescriptor(jobId);
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
		Tuple<JobDescriptor, JobStatus> job = getJobDescriptor(jobId);
		if (job != null) {
			job.getX().setOwner(uid);
			updateJob(granter, job.getX(), uid, job.getX().getGroupId());
		}
	}

	@Override
	public void moveJob(String uid, String jobId, String groupId)
			throws ZeusException {
		JobDescriptor jd = getJobDescriptor(jobId).getX();
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
	
	@Override
	public void saveJob(JobPersistence actionPer) throws ZeusException{
		try{
			JobPersistence action = (JobPersistence)getHibernateTemplate().get(JobPersistence.class, actionPer.getId());
			if(action != null){
					if(action.getStatus() == null || !action.getStatus().equalsIgnoreCase("running")){
						actionPer.setHistoryId(action.getHistoryId());
						actionPer.setReadyDependency(action.getReadyDependency());
						actionPer.setStatus(action.getStatus());
					}else{
						actionPer = action;
					}
			}else{
				SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
				String currentDateStr = df.format(new Date())+"0000";
				if (actionPer.getId() < Long.parseLong(currentDateStr)) {
					actionPer.setStatus("failed");
				}
			}
			if(actionPer.getAuto() == 0){
				if(actionPer.getStatus() == null || actionPer.getStatus().equalsIgnoreCase("wait")){
					actionPer.setStatus("failed");
				}
			}
			getHibernateTemplate().saveOrUpdate(actionPer);			
		}catch(DataAccessException e){
			throw new ZeusException(e);
		}
	}
	
	@Override
	public List<JobPersistence> getLastJobAction(String jobId) {
		List<JobPersistence> list = getHibernateTemplate().find(
				"from com.taobao.zeus.store.mysql.persistence.JobPersistence where toJobId='"+jobId+"' order by id desc");
		return list;
	}


	@Override
	public List<Tuple<JobDescriptor, JobStatus>> getActionList(String jobId) {
		List<JobPersistence> list = getHibernateTemplate().find(
				"from com.taobao.zeus.store.mysql.persistence.JobPersistence where toJobId='"+jobId+"' order by id desc");
		List<Tuple<JobDescriptor, JobStatus>> lst = new ArrayList<Tuple<JobDescriptor, JobStatus>>();
		for(JobPersistence persist : list){
			lst.add(PersistenceAndBeanConvert.convert(persist));
		}
		return lst;
	}

	@Override
	public void updateAction(JobDescriptor actionTor) throws ZeusException {
		try{
			JobPersistence actionPer = PersistenceAndBeanConvert.convert(actionTor);
			JobPersistence action = (JobPersistence)getHibernateTemplate().get(JobPersistence.class, actionPer.getId());
			if(action != null){
					if(action.getStatus() == null || !action.getStatus().equalsIgnoreCase("running")){
						actionPer.setHistoryId(action.getHistoryId());
						actionPer.setReadyDependency(action.getReadyDependency());
						actionPer.setStatus(action.getStatus());
					}else{
						actionPer = action;
					}
			}
			getHibernateTemplate().saveOrUpdate(actionPer);
		}catch(DataAccessException e){
			throw new ZeusException(e);
		}
		
	}

	@Override
	public void removeJob(Long actionId) throws ZeusException{
		try{
			JobPersistence action = (JobPersistence)getHibernateTemplate().get(JobPersistence.class, actionId);
			if(action != null){
				getHibernateTemplate().delete(action);
			}
		}catch(DataAccessException e){
			throw new ZeusException(e);
		}
	}

}