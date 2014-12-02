package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.model.ZeusFollow;
import com.taobao.zeus.store.FollowManagerOld;
import com.taobao.zeus.store.GroupBean;
import com.taobao.zeus.store.GroupBeanOld;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.GroupManagerOld;
import com.taobao.zeus.store.JobBean;
import com.taobao.zeus.store.JobBeanOld;
import com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvert;
@SuppressWarnings("unchecked")
public class MysqlFollowManagerOld extends HibernateDaoSupport implements FollowManagerOld{

	
	@Override
	public List<ZeusFollow> findAllTypeFollows(final String uid) {
		List<ZeusFollowPersistence> list= (List<ZeusFollowPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence where uid=?");
				query.setParameter(0, uid);
				return query.list();
			}
		});
		
		List<ZeusFollow> result=new ArrayList<ZeusFollow>();
		if(list!=null){
			for(ZeusFollowPersistence persist:list){
				result.add(PersistenceAndBeanConvert.convert(persist));
			}
		}
		return result;
	}

	@Override
	public List<ZeusFollow> findFollowedGroups(final String uid) {
		List<ZeusFollowPersistence> list=  (List<ZeusFollowPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence where type="+ZeusFollow.GroupType+" uid=?");
				query.setParameter(0, uid);
				return query.list();
			}
		});
		List<ZeusFollow> result=new ArrayList<ZeusFollow>();
		if(list!=null){
			for(ZeusFollowPersistence persist:list){
				result.add(PersistenceAndBeanConvert.convert(persist));
			}
		}
		return result;
	}

	@Override
	public List<ZeusFollow> findFollowedJobs(final String uid) {
		List<ZeusFollowPersistence> list=  (List<ZeusFollowPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence where type="+ZeusFollow.JobType+" and uid=?");
				query.setParameter(0, uid);
				return query.list();
			}
		});
		List<ZeusFollow> result=new ArrayList<ZeusFollow>();
		if(list!=null){
			for(ZeusFollowPersistence persist:list){
				result.add(PersistenceAndBeanConvert.convert(persist));
			}
		}
		return result;
	}

	@Override
	public List<ZeusFollow> findJobFollowers(final String jobId) {
		List<ZeusFollowPersistence> list=  (List<ZeusFollowPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence where type="+ZeusFollow.JobType+" and targetId=?");
				query.setParameter(0, Long.valueOf(jobId));
				return query.list();
			}
		});
		List<ZeusFollow> result=new ArrayList<ZeusFollow>();
		if(list!=null){
			for(ZeusFollowPersistence persist:list){
				result.add(PersistenceAndBeanConvert.convert(persist));
			}
		}
		return result;
	}

	@Override
	public List<ZeusFollow> findGroupFollowers(final List<String> groupIds) {
		List<ZeusFollowPersistence> list= (List<ZeusFollowPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				if(groupIds.isEmpty()){
					return Collections.emptyList();
				}
				List<Long> ids=new ArrayList<Long>();
				for(String group:groupIds){
					ids.add(Long.valueOf(group));
				}
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence where type="+ZeusFollow.GroupType+" and targetId in (:list)");
				query.setParameterList("list", ids);
				return query.list();
			}
		});
		List<ZeusFollow> result=new ArrayList<ZeusFollow>();
		if(list!=null){
			for(ZeusFollowPersistence persist:list){
				result.add(PersistenceAndBeanConvert.convert(persist));
			}
		}
		return result;
	}

	@Override
	public ZeusFollow addFollow(final String uid, final Integer type, final String targetId) {
		List<ZeusFollowPersistence> list=(List<ZeusFollowPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence where uid=? and type=? and targetId=?");
				query.setParameter(0, uid);
				query.setParameter(1, type);
				query.setParameter(2, Long.valueOf(targetId));
				return query.list();
			} 
		});
		if(list!=null && !list.isEmpty()){
			ZeusFollow zf=PersistenceAndBeanConvert.convert(list.get(0));
			return zf;
		}
		ZeusFollowPersistence persist=new ZeusFollowPersistence();
		persist.setGmtCreate(new Date());
		persist.setGmtModified(new Date());
		persist.setTargetId(Long.valueOf(targetId));
		persist.setType(type);
		persist.setUid(uid);
		getHibernateTemplate().save(persist);
		
		return PersistenceAndBeanConvert.convert(persist);
	}

	@Override
	public void deleteFollow(final String uid, final Integer type, final String targetId) {
		List<ZeusFollowPersistence> list=(List<ZeusFollowPersistence>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusFollowPersistence where uid=? and type=? and targetId=?");
				query.setParameter(0, uid);
				query.setParameter(1, type);
				query.setParameter(2, Long.valueOf(targetId));
				return query.list();
			}
		});
		if(list!=null && !list.isEmpty()){
			for(ZeusFollowPersistence persist:list){
				getHibernateTemplate().delete(persist);
			}
		}
	}
	@Autowired
	@Qualifier("groupManagerOld")
	private GroupManagerOld groupManagerOld;
	@Override
	public List<String> findActualJobFollowers(String jobId) {
		List<ZeusFollow> jobFollows=findJobFollowers(jobId);
		JobBeanOld jobBean=groupManagerOld.getUpstreamJobBean(jobId);
		
		List<String> groupIds=new ArrayList<String>();
		GroupBeanOld gb=jobBean.getGroupBean();
		while(gb!=null){
			groupIds.add(gb.getGroupDescriptor().getId());
			gb=gb.getParentGroupBean();
		}
		List<ZeusFollow> groupFollows=findGroupFollowers(groupIds);
		
		List<String> follows=new ArrayList<String>();
		//任务创建人自动纳入消息通知人员名单
		follows.add(jobBean.getJobDescriptor().getOwner());
		for(ZeusFollow zf:jobFollows){
			if(!follows.contains(zf.getUid())){
				follows.add(zf.getUid());
			}
		}
		for(ZeusFollow zf:groupFollows){
			if(!follows.contains(zf.getUid())){
				follows.add(zf.getUid());
			}
		}
		return follows;
	}

}
