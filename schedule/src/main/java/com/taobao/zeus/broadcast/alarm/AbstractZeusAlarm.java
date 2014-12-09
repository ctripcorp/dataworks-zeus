package com.taobao.zeus.broadcast.alarm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.model.JobStatus.TriggerType;
import com.taobao.zeus.schedule.mvc.JobFailListener.ChainException;
import com.taobao.zeus.store.FollowManager;
import com.taobao.zeus.store.FollowManagerOld;
import com.taobao.zeus.store.GroupManager;
import com.taobao.zeus.store.GroupManagerOld;
import com.taobao.zeus.store.JobHistoryManager;
import com.taobao.zeus.store.UserManager;

public abstract class AbstractZeusAlarm implements ZeusAlarm{
	protected static Logger log=LoggerFactory.getLogger(AbstractZeusAlarm.class);
	@Autowired
	protected JobHistoryManager jobHistoryManager;
	@Autowired
	@Qualifier("followManagerOld")
	protected FollowManagerOld followManagerOld;
	@Autowired
	@Qualifier("groupManagerOld")
	protected GroupManagerOld groupManagerOld;
	@Override
	public void alarm(String historyId, String title, String content,ChainException chain)
			throws Exception {
		JobHistory history=jobHistoryManager.findJobHistory(historyId);
		TriggerType type=history.getTriggerType();
		//获得action_id
		String jobId=history.getJobId();
		//获得job_id
		String tojobId=history.getToJobId();
		List<String> users=new ArrayList<String>();
		if(type==TriggerType.SCHEDULE){
			users=followManagerOld.findActualJobFollowers(tojobId);
		}else{
			users.add(groupManagerOld.getJobDescriptor(tojobId).getX().getOwner());
			if(history.getOperator()!=null){
				if(!users.contains(history.getOperator())){
					users.add(history.getOperator());
				}
			}
		}
		List<String> result=new ArrayList<String>();
		if(chain==null){
			result=users;
		}else{
			for(String uid:users){
				Integer count=chain.getUserCountMap().get(uid);
				if(count==null){
					count=1;
					chain.getUserCountMap().put(uid, count);
				}
				if(count<20){//一个job失败，最多发给同一个人20个报警
					chain.getUserCountMap().put(uid, ++count);
					result.add(uid);
				}
			}
		}
		alarm(jobId, result, title, content);
	}

	@Override
	public void alarm(String historyId, String title, String content)
			throws Exception {
		alarm(historyId, title, content, null);
	}
	/**
	 * @param jobId anction_id
	 * @param users 用户域账号id
	 * @param title
	 * @param content
	 * @throws Exception
	 */
	public abstract void alarm(String jobId, List<String> users,String title,String content) throws Exception;

}