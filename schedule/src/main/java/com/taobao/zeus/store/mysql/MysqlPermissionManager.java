package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.store.GroupBean;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.PermissionManager;
import com.taobao.zeus.store.Super;
import com.taobao.zeus.store.mysql.persistence.PermissionPersistence;
@SuppressWarnings("unchecked")
public class MysqlPermissionManager extends HibernateDaoSupport implements PermissionManager{
	@Autowired
	@Qualifier("groupManager")
	private GroupManager groupManager;
	@Override
	public Boolean hasGroupPermission(final String user, final String groupId) {
		if(Super.getSupers().contains(user)){
			//超级管理员
			return true;
		}
		Set<String> groups=new HashSet<String>();
		GroupBean gb=groupManager.getUpstreamGroupBean(groupId);
		if(user.equals(gb.getGroupDescriptor().getOwner())){
			//组所有人
			return true;
		}
		while(gb!=null){
			groups.add(gb.getGroupDescriptor().getId());
			gb=gb.getParentGroupBean();
		}
		Set<String> users=new HashSet<String>();
		for(String g:groups){
			users.addAll(getGroupAdmins(g));
		}
		return users.contains(user)?true:false;
	}
	
	public List<String> getGroupAdmins(final String groupId){
		return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("select uid from com.taobao.zeus.store.mysql.persistence.PermissionPersistence where type=? and targetId=?");
				query.setParameter(0, PermissionPersistence.GROUP_TYPE);
				query.setParameter(1, Long.valueOf(groupId));
				return query.list();
			}
		});
	}
	public List<String> getJobAdmins(final String jobId){
		return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("select uid from com.taobao.zeus.store.mysql.persistence.PermissionPersistence where type=? and targetId=?");
				query.setParameter(0, PermissionPersistence.JOB_TYPE);
				query.setParameter(1, Long.valueOf(jobId));
				return query.list();
			}
		});
	}
	
	private PermissionPersistence getGroupPermission(final String user,final String groupId){
		List<PermissionPersistence> list=(List<PermissionPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.PermissionPersistence where type=? and uid=? and targetId=?");
				query.setParameter(0,PermissionPersistence.GROUP_TYPE);
				query.setParameter(1, user);
				query.setParameter(2, Long.valueOf(groupId));
				return query.list();
			}
		});
		if(list!=null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	private PermissionPersistence getJobPermission(final String user,final String jobId){
		List<PermissionPersistence> list=(List<PermissionPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.PermissionPersistence where type=? and uid=? and targetId=?");
				query.setParameter(0,PermissionPersistence.JOB_TYPE);
				query.setParameter(1, user);
				query.setParameter(2, Long.valueOf(jobId));
				return query.list();
			}
		});
		if(list!=null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	@Override
	public void addGroupAdmin(String user,String groupId) {
		boolean has=getGroupPermission(user, groupId)==null?false:true;
		if(!has){
			PermissionPersistence pp=new PermissionPersistence();
			pp.setType(PermissionPersistence.GROUP_TYPE);
			pp.setUid(user);
			pp.setTargetId(Long.valueOf(groupId));
			pp.setGmtModified(new Date());
			getHibernateTemplate().save(pp);
		}
	}
	@Override
	public void addJobAdmin(String user, String jobId) {
		boolean has=getJobPermission(user, jobId)==null?false:true;
		if(!has){
			PermissionPersistence pp=new PermissionPersistence();
			pp.setType(PermissionPersistence.JOB_TYPE);
			pp.setUid(user);
			pp.setTargetId(Long.valueOf(jobId));
			pp.setGmtModified(new Date());
			getHibernateTemplate().save(pp);
		}
	}
	@Override
	public Boolean hasJobPermission(String user, String jobId) {
		if(Super.getSupers().contains(user)){
			//超级管理员
			return true;
		}
		Set<String> groups=new HashSet<String>();
		JobBean jobBean=groupManager.getUpstreamJobBean(jobId);
		if(user.equals(jobBean.getJobDescriptor().getOwner())){
			//任务所有人
			return true;
		}
		GroupBean gb=jobBean.getGroupBean();
		while(gb!=null){
			groups.add(gb.getGroupDescriptor().getId());
			gb=gb.getParentGroupBean();
		}
		Set<String> users=new HashSet<String>();
		users.addAll(getJobAdmins(jobId));
		for(String g:groups){
			users.addAll(getGroupAdmins(g));
		}
		return users.contains(user)?true:hasGroupPermission(user, groupManager.getJobDescriptor(jobId).getX().getGroupId());
	}
	@Override
	public void removeGroupAdmin(String user, String groupId) {
		PermissionPersistence pp=getGroupPermission(user, groupId);
		if(pp!=null){
			getHibernateTemplate().delete(pp);
		}
	}
	@Override
	public void removeJobAdmin(String user, String jobId) {
		PermissionPersistence pp=getJobPermission(user, jobId);
		if(pp!=null){
			getHibernateTemplate().delete(pp);
		}
	}

}