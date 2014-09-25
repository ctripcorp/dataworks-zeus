package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.client.ZeusException;
import com.taobao.zeus.model.GroupDescriptor;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobDescriptor.JobRunType;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.store.GroupBean;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.GroupManagerTool;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.mysql.persistence.GroupPersistence;
import com.taobao.zeus.store.mysql.persistence.JobPersistence;
import com.taobao.zeus.store.mysql.persistence.Worker;
import com.taobao.zeus.store.mysql.tool.Judge;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvert;
import com.taobao.zeus.util.Tuple;

public class CacheMysqlGroupManager extends HibernateDaoSupport implements GroupManager{
	private Judge jobjudge=new Judge();
	private Judge groupjudge=new Judge();
	private Map<String, JobPersistence> cacheJobMap=new HashMap<String,JobPersistence>();
	private Map<String, GroupPersistence> cacheGroupMap=new HashMap<String, GroupPersistence>();
	
	private GroupManager groupManager;
	private Map<String, JobPersistence> getCacheJobs(){
		Judge realtime=(Judge) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Object[] o=(Object[]) session.createSQLQuery("select count(*),max(id),max(gmt_modified) from zeus_job").uniqueResult();
				if(o!=null){
					Judge j=new Judge();
					j.count=((Number) o[0]).intValue();
					j.maxId=((Number)o[1]).intValue();
					j.lastModified=(Date) o[2];
					j.stamp=new Date();
					return j;
				}
				return null;
			}
		});
		
		if(realtime!=null && realtime.count.equals(jobjudge.count) && realtime.maxId.equals(jobjudge.maxId) && realtime.lastModified.equals(jobjudge.lastModified)){
			jobjudge.stamp=new Date();
			return cacheJobMap;
		}else{
			List<JobPersistence> list=getHibernateTemplate().find("from com.taobao.zeus.store.mysql.persistence.JobPersistence");
			Map<String, JobPersistence> newmap=new HashMap<String, JobPersistence>();
			for(JobPersistence p:list){
				newmap.put(p.getId().toString(), p);
			}
			cacheJobMap=newmap;
			jobjudge=realtime;
			return cacheJobMap;
		}
	}
	private Map<String, GroupPersistence> getCacheGroups(){
		Judge realtime=(Judge) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Object[] o=(Object[]) session.createSQLQuery("select count(*),max(id),max(gmt_modified) from zeus_group").uniqueResult();
				if(o!=null){
					Judge j=new Judge();
					j.count=((Number) o[0]).intValue();
					j.maxId=((Number)o[1]).intValue();
					j.lastModified=(Date) o[2];
					j.stamp=new Date();
					return j;
				}
				return null;
			}
		});
		if(realtime!=null && realtime.count.equals(groupjudge.count) && realtime.maxId.equals(groupjudge.maxId) && realtime.lastModified.equals(groupjudge.lastModified)){
			groupjudge.stamp=new Date();
			return cacheGroupMap;
		}else{
			List<GroupPersistence> list=getHibernateTemplate().find("from com.taobao.zeus.store.mysql.persistence.GroupPersistence");
			Map<String, GroupPersistence> newmap=new HashMap<String, GroupPersistence>();
			for(GroupPersistence p:list){
				newmap.put(p.getId().toString(),p);
			}
			cacheGroupMap=newmap;
			groupjudge=realtime;
			return cacheGroupMap;
		}
	}
	
	@Override
	public GroupDescriptor createGroup(String user, String groupName,
			String parentGroup, boolean isDirectory) throws ZeusException {
		return groupManager.createGroup(user, groupName, parentGroup, isDirectory);
	}

	@Override
	public JobDescriptor createJob(String user, String jobName,
			String parentGroup, JobRunType jobType) throws ZeusException {
		return groupManager.createJob(user, jobName, parentGroup, jobType);
	}

	@Override
	public void deleteGroup(String user, String groupId) throws ZeusException {
		groupManager.deleteGroup(user, groupId);
	}

	@Override
	public void deleteJob(String user, String jobId) throws ZeusException {
		groupManager.deleteJob(user, jobId);
	}

	@Override
	public List<GroupDescriptor> getChildrenGroup(String groupId) {
		List<GroupDescriptor> list=new ArrayList<GroupDescriptor>();
		Map<String, GroupPersistence> map=getCacheGroups();
		for(GroupPersistence p:map.values()){
			if(p.getParent()!=null && p.getParent().toString().equals(groupId)){
				list.add(PersistenceAndBeanConvert.convert(p));
			}
		}
		return list;
	}

	@Override
	public List<Tuple<JobDescriptor, JobStatus>> getChildrenJob(String groupId) {
		List<Tuple<JobDescriptor, JobStatus>> list=new ArrayList<Tuple<JobDescriptor,JobStatus>>();
		Map<String, JobPersistence> map=getCacheJobs();
		for(JobPersistence p:map.values()){
			if(p.getGroupId().toString().equals(groupId)){
				list.add(PersistenceAndBeanConvert.convert(p));
			}
		}
		return list;
	}

	@Override
	public GroupBean getDownstreamGroupBean(String groupId) {
		GroupDescriptor group=getGroupDescriptor(groupId);
		GroupBean result=new GroupBean(group);
		return getDownstreamGroupBean(result);
	}

	@Override
	public GroupBean getDownstreamGroupBean(GroupBean parent) {
		if(parent.isDirectory()){
			List<GroupDescriptor> children=getChildrenGroup(parent.getGroupDescriptor().getId());
			for(GroupDescriptor child:children){
				GroupBean childBean=new GroupBean(child);
				getDownstreamGroupBean(childBean);
				childBean.setParentGroupBean(parent);
				parent.getChildrenGroupBeans().add(childBean);
			}
		}else{
			List<Tuple<JobDescriptor, JobStatus>> jobs=getChildrenJob(parent.getGroupDescriptor().getId());
			for(Tuple<JobDescriptor, JobStatus> tuple:jobs){
				JobBean jobBean=new JobBean(tuple.getX(),tuple.getY());
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

	@Override
	public GroupDescriptor getGroupDescriptor(String groupId) {
		Map<String, GroupPersistence> map=getCacheGroups();
		return PersistenceAndBeanConvert.convert(map.get(groupId));
	}

	@Override
	public Tuple<JobDescriptor, JobStatus> getJobDescriptor(String jobId) {
		return PersistenceAndBeanConvert.convert(getCacheJobs().get(jobId));
	}

	@Override
	public Map<String, Tuple<JobDescriptor, JobStatus>> getJobDescriptor(
			Collection<String> jobIds) {
		Map<String, JobPersistence> map=getCacheJobs();
		Map<String, Tuple<JobDescriptor, JobStatus>> result=new HashMap<String, Tuple<JobDescriptor,JobStatus>>();
		for(String id:jobIds){
			result.put(id,PersistenceAndBeanConvert.convert(map.get(id)));
		}
		return result;
	}

	@Override
	public JobStatus getJobStatus(String jobId) {
		Tuple<JobDescriptor, JobStatus> job=PersistenceAndBeanConvert.convert(getCacheJobs().get(jobId));
		if(job==null){
			return null;
		}
		return job.getY();
	}

	@Override
	public String getRootGroupId() {
		return groupManager.getRootGroupId();
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
	public void grantGroupOwner(String granter, String uid, String groupId)
			throws ZeusException {
		groupManager.grantGroupOwner(granter, uid, groupId);
	}

	@Override
	public void grantJobOwner(String granter, String uid, String jobId)
			throws ZeusException {
		groupManager.grantJobOwner(granter, uid, jobId);
	}

	@Override
	public void updateGroup(String user, GroupDescriptor group)
			throws ZeusException {
		groupManager.updateGroup(user, group);
	}

	@Override
	public void updateJob(String user, JobDescriptor job) throws ZeusException {
		groupManager.updateJob(user, job);
	}

	@Override
	public void updateJobStatus(JobStatus jobStatus) {
		groupManager.updateJobStatus(jobStatus);
	}
	public void setGroupManager(GroupManager groupManager) {
		this.groupManager = groupManager;
	}
	@Override
	public void moveJob(String uid, String jobId, String groupId)
			throws ZeusException {
		groupManager.moveJob(uid, jobId, groupId);
	}
	@Override
	public void moveGroup(String uid, String groupId, String newParentGroupId)
			throws ZeusException {
		groupManager.moveGroup(uid, groupId, newParentGroupId);
	}
	@Override
	public List<String> getHosts() throws ZeusException {
		return Collections.emptyList();
	}
	@Override
	public void replaceWorker(Worker worker) throws ZeusException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeWorker(String host) throws ZeusException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void saveJob(JobPersistence actionPer) throws ZeusException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public List<JobPersistence> getLastJobAction(String jobId){
		// TODO Auto-generated method stub
		return null;
	}

}
