package com.taobao.zeus.store.mysql;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.model.LogDescriptor;
import com.taobao.zeus.store.mysql.persistence.LogPersistence;

@SuppressWarnings("unchecked")
public class MysqlLogManager extends HibernateDaoSupport {
	
	public void addLog(LogDescriptor logDescriptor) {
		try {
			LogPersistence logPersistence = new LogPersistence();
//			logPersistence.setId(11L);
			logPersistence.setLogType(logDescriptor.getLogType());
			logPersistence.setUserName(logDescriptor.getUserName());
			logPersistence.setIp(logDescriptor.getIp());
			logPersistence.setUrl(logDescriptor.getUrl());
			logPersistence.setRpc(logDescriptor.getRpc());
			logPersistence.setDelegate(logDescriptor.getDelegate());
			logPersistence.setMethod(logDescriptor.getMethod());
			logPersistence.setDescription(logDescriptor.getDescription());
			super.getHibernateTemplate().save(logPersistence);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
