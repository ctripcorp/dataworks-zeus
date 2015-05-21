package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.model.HostGroupCache;
import com.taobao.zeus.store.HostGroupManager;
import com.taobao.zeus.store.mysql.persistence.HostGroupPersistence;
import com.taobao.zeus.store.mysql.persistence.HostRelationPersistence;
import com.taobao.zeus.util.Environment;
@SuppressWarnings("unchecked")
public class MysqlHostGroupManager extends HibernateDaoSupport implements HostGroupManager{

	public List<HostRelationPersistence> getAllHostRelations() {
		return (List<HostRelationPersistence>)getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery("from com.taobao.zeus.store.mysql.persistence.HostRelationPersistence");
				return query.list();
			}
			
		});		
	}
	
	public List<HostRelationPersistence> getHostRelations(final String hostGroupId) {
		return (List<HostRelationPersistence>)getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery("from com.taobao.zeus.store.mysql.persistence.HostRelationPersistence where hostGroupId=" + hostGroupId);
				return query.list();
			}
			
		});		
	}
	

	@Override
	public Map<String,HostGroupCache> getAllHostGroupInfomations() {
		Map<String,HostGroupCache> informations = new HashMap<String,HostGroupCache>();
		List<HostGroupPersistence> hostgroups = getAllHostGroup();
		List<HostRelationPersistence> relations = getAllHostRelations();
		for(HostGroupPersistence wg : hostgroups){
			if (wg.getEffective() == 0) {
				continue;
			}
			HostGroupCache info = new HostGroupCache();
			String id = wg.getId().toString();
			info.setId(id);
			info.setCurrentPositon(0);
			info.setName(wg.getName());
			info.setDescription(wg.getDescription());
			List<String> hosts = new ArrayList<String>();
			for(HostRelationPersistence r : relations){
				if (wg.getId().equals(r.getHostGroupId())) {
					hosts.add(r.getHost());
				}
			}
			info.setHosts(hosts);
//			informations.add(info);
			informations.put(id, info);
		}
		return informations;
	}
	@Override
	public List<HostGroupPersistence> getAllHostGroup(){
		return (List<HostGroupPersistence>)getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery("from com.taobao.zeus.store.mysql.persistence.HostGroupPersistence");
				return query.list();
			}
			
		});	
	}

	@Override
	public HostGroupPersistence getHostGroupName(String hostGroupId) {
		return (HostGroupPersistence) getHibernateTemplate().get(HostGroupPersistence.class, Integer.valueOf(hostGroupId));
	}

	@Override
	public List<String> getPreemptionHost() {
		String id = Environment.getDefaultMasterGroupId();
		List<HostRelationPersistence> hostRelations = getHostRelations(id);
		List<String> result = new ArrayList<String>();
		for(HostRelationPersistence hostRalation : hostRelations){
			result.add(hostRalation.getHost());
		}
		return result;
	}

}
