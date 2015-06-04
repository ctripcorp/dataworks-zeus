package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
@SuppressWarnings("unchecked")
public class MysqlUserManager extends HibernateDaoSupport implements UserManager{
	
	public List<ZeusUser> getAllUsers(){
		return (List<ZeusUser>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusUser where isEffective=1 ");
				return query.list();
			}
		});
	}

	
	public ZeusUser findByUid(String uid){
		DetachedCriteria criteria=DetachedCriteria.forClass(ZeusUser.class);
		criteria.add(Expression.eq("uid", uid));
		List<ZeusUser> users=getHibernateTemplate().findByCriteria(criteria);
		if(users!=null && !users.isEmpty()){
			return users.get(0);
		}
		return null;
	}
	
	public List<ZeusUser> findListByUid(final List<String> uids){
		if(uids.isEmpty()){
			return new ArrayList<ZeusUser>();
		}
		List<ZeusUser> list = (List<ZeusUser>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusUser where uid in (:idList)");
				query.setParameterList("idList", uids);
				return query.list();
			}
		});
		return list;
	} 
	
	public ZeusUser addOrUpdateUser(final ZeusUser user){
		List<ZeusUser> list=(List<ZeusUser>) getHibernateTemplate().execute(new HibernateCallback() {
			
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusUser where uid=?");
				query.setParameter(0, user.getUid());
				return query.list();
			}
		});
		if(list!=null && !list.isEmpty()){
			ZeusUser zu=list.get(0);
			zu.setEmail(user.getEmail());
			zu.setWangwang(user.getWangwang());
			zu.setName(user.getName());
			if(user.getPhone()!=null && !"".equals(user.getPhone())){
				zu.setPhone(user.getPhone());
			}
			zu.setGmtModified(new Date());
			zu.setIsEffective(user.getIsEffective());
			zu.setUserType(user.getUserType());
			zu.setDescription(user.getDescription());
			getHibernateTemplate().update(zu);
		}else{
			user.setGmtCreate(new Date());
			user.setGmtModified(new Date());
			getHibernateTemplate().save(user);
		}
		return user;
	}

	@Override
	public List<ZeusUser> findListByUidByOrder(final List<String> uids) {
		List<ZeusUser> result = new ArrayList<ZeusUser>();
		if(uids.isEmpty()){
			return result;
		}
		List<ZeusUser> users = findListByUid(uids);
		for(String uid : uids){
			for(ZeusUser user : users){
				if (uid.equals(user.getUid())) {
					result.add(user);
				}
			}
		}
		return result;
	}
	
	/**2015-02-04**/
	public ZeusUser findByUidFilter(String uid){
		DetachedCriteria criteria=DetachedCriteria.forClass(ZeusUser.class);
		criteria.add(Expression.eq("uid", uid));
		criteria.add(Expression.eq("isEffective", 1));
		List<ZeusUser> users=getHibernateTemplate().findByCriteria(criteria);
		if(users!=null && !users.isEmpty()){
			return users.get(0);
		}
		return null;
	}
	
	public List<ZeusUser> findAllUsers(final String sortField, final String sortOrder){
		return (List<ZeusUser>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusUser order by " + sortField + " " + sortOrder);
				return query.list();
			}
		});
	}
	
	public List<ZeusUser> findListByFilter(final String filter, final String sortField, final String sortOrder){
		if(filter.isEmpty()){
			return new ArrayList<ZeusUser>();
		}
		List<ZeusUser> list = (List<ZeusUser>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Query query=session.createQuery("from com.taobao.zeus.store.mysql.persistence.ZeusUser "+
					"where uid like :uid or name like :name or email like :email order by " + sortField + " " + sortOrder);
				query.setString("uid", "%"+filter+"%");
				query.setString("name", "%"+filter+"%");
				query.setString("email", "%"+filter+"%");
				return query.list();
			}
		});
		return list;
	} 

	@Override
	public void update(ZeusUser user) {
		user.setGmtModified(new Date());
		getHibernateTemplate().update(user);
	}
}
