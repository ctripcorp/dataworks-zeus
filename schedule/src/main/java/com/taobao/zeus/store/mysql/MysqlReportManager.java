package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MysqlReportManager extends HibernateDaoSupport{

	/**
	 * yyyyMMdd->{success:1,fail:2},{success:1,fail:2}
	 * @param start
	 * @param end
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, String>> runningJobs(final Date start,final Date end){
		return (Map<String, Map<String, String>>) getHibernateTemplate().execute(new HibernateCallback() {
			private SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				Map<String, Map<String, String>> result=new HashMap<String, Map<String,String>>();
				
				String success_sql="select count(distinct h.action_id),h.gmt_create from zeus_action_history h " +
						"left join zeus_action j on h.action_id=j.id " +
						"where h.status='success' and trigger_type=1 and to_days(h.gmt_create) between to_days(?) and to_days(?) "+
						"group by to_days(h.gmt_create) order by h.gmt_create desc";
				SQLQuery query=session.createSQLQuery(success_sql);
				query.setParameter(0, start);
				query.setParameter(1, end);
				List success_list=query.list();
				for(Object o:success_list){
					Object[] oo=(Object[])o;
					Date date=(Date)oo[1];
					Number success=(Number)oo[0];
					Map<String, String> map=new HashMap<String, String>();
					map.put("success", success.toString());
					result.put(format.format(date), map);
				}
				
				String fail_sql="select count(distinct h.action_id),h.gmt_create from zeus_action_history h " +
					"left join zeus_action j on h.action_id=j.id " +
					"where h.status='failed' and trigger_type=1 and to_days(h.gmt_create) between to_days(?) and to_days(?) "+
					"and h.action_id not in (select action_id from zeus_action_history where status='success' and trigger_type=1 and to_days(gmt_create) between to_days(?) and to_days(?)) "+
					"group by to_days(h.gmt_create) order by h.gmt_create desc";
				query=session.createSQLQuery(fail_sql);
				query.setDate(0, start);
				query.setDate(1, end);
				query.setDate(2, start);
				query.setDate(3, end);
				List fail_list=query.list();
				for(Object o:fail_list){
					Object[] oo=(Object[])o;
					Date date=(Date)oo[1];
					Number fail=(Number)oo[0];
					Map<String, String> map=result.get(format.format(date));
					if(map==null){
						map=new HashMap<String, String>();
						result.put(format.format(date), map);
					}
					map.put("fail", fail.toString());
				}
				
				return result;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> ownerFailJobs(final Date date){
		List<Map<String, String>> list=(List<Map<String, String>>) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException,
					SQLException {
				List<Map<String, String>> result=new ArrayList<Map<String,String>>();
				
				String sql="select count(distinct h.action_id) as cou,j.owner,u.name from zeus_action_history h " +
						"left join zeus_action j on h.action_id=j.id " +
						"left join zeus_user u on j.owner=u.uid " +
						"where h.status='failed' and h.trigger_type=1 " +
						"and to_days(?)=to_days(h.gmt_create) "+
						"and h.action_id not in (select action_id from zeus_action_history where status='success' and trigger_type=1 and to_days(?)=to_days(gmt_create)) "+
						"group by j.owner order by cou desc limit 10";
				SQLQuery query=session.createSQLQuery(sql);
				query.setDate(0, date);
				query.setDate(1, date);
				List list=query.list();
				for(Object o:list){
					Object[] oo=(Object[])o;
					Number num=(Number)oo[0];
					String uid=(String)oo[1];
					String uname=(String)oo[2];
					Map<String, String> map=new HashMap<String, String>();
					map.put("count", num.toString());
					map.put("uid", uid);
					map.put("uname", uname);
					result.add(map);
				}
				return result;
			}
		});
		
		for(final Map<String, String> map:list){
			getHibernateTemplate().execute(new HibernateCallback() {
				@Override
				public Object doInHibernate(Session session) throws HibernateException,
						SQLException {
					String sql="select distinct h.action_id,j.name from zeus_action_history h " +
							"left join zeus_action j on h.action_id=j.id where h.status='failed' " +
							"and h.trigger_type=1 and to_days(?) =to_days(h.gmt_create) and j.owner=? "+
							"and h.action_id not in (select action_id from zeus_action_history where status='success' and trigger_type=1 and to_days(?)=to_days(gmt_create))";
					SQLQuery query=session.createSQLQuery(sql);
					query.setDate(0, date);
					query.setString(1, map.get("uid"));
					query.setDate(2, date);
					List<Object[]> list=query.list();
					int count=0;
					for(Object[] rs:list){
						String jobID = rs[0].toString();
						String jobName = rs[1].toString();
						// 去重
						if(!map.containsKey(jobID)){
							map.put("history"+count++, jobName+"("+jobID+")");
						}
						map.put(jobID, null);
					}
					map.put("count", count+"");
					return null;
				}
			});
		}
		return list;
	}
}
