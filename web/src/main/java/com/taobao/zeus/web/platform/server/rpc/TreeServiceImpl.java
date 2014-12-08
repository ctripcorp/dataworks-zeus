package com.taobao.zeus.web.platform.server.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.zeus.model.JobHistory;
import com.taobao.zeus.model.ZeusFollow;
import com.taobao.zeus.store.FollowManager;
import com.taobao.zeus.store.FollowManagerOld;
import com.taobao.zeus.store.GroupBeanOld;
import com.taobao.zeus.store.JobBeanOld;
import com.taobao.zeus.store.JobHistoryManager;
import com.taobao.zeus.store.mysql.ReadOnlyGroupManagerOld;
import com.taobao.zeus.web.LoginUser;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupJobTreeModel;
import com.taobao.zeus.web.platform.shared.rpc.TreeService;

public class TreeServiceImpl implements TreeService{
	@Autowired
	private ReadOnlyGroupManagerOld readOnlyGroupManager;
	@Autowired
	private FollowManagerOld followManagerOld;
	@Autowired
	private JobHistoryManager jobHistoryManager;
	
	private static Logger log=LoggerFactory.getLogger(TreeServiceImpl.class);
	@Override
	public GroupJobTreeModel getMyTreeData() {
		String uid=LoginUser.getUser().getUid();
		GroupBeanOld rootGroup=readOnlyGroupManager.getGlobeGroupBeanForTreeDisplay(true);
		Map<String, JobBeanOld> allJobs=rootGroup.getAllSubJobBeans();
		for(String key:allJobs.keySet()){
			JobBeanOld bean=allJobs.get(key);
			//不是owner，删除
			if(!bean.getJobDescriptor().getOwner().equals(uid)){
				bean.getGroupBean().getJobBeans().remove(key);
			}
		}
		
		Map<String, GroupBeanOld> allGroups=rootGroup.getAllSubGroupBeans();
		List<GroupBeanOld> leafGroups=new ArrayList<GroupBeanOld>();
		for(GroupBeanOld bean:allGroups.values()){
			if(!bean.isDirectory() || bean.getChildrenGroupBeans().isEmpty()){
				leafGroups.add(bean);
			}
		}
		for(GroupBeanOld bean:leafGroups){
			recursionRemove(bean, uid);
		}
		
		return getTreeData(rootGroup);
	}
	private void recursionRemove(GroupBeanOld bean,String uid){
		if(!bean.isDirectory()){
			if(!bean.getGroupDescriptor().getOwner().equals(uid) && bean.getJobBeans().isEmpty()){
				GroupBeanOld parent=bean.getParentGroupBean();
				if(parent!=null){
					parent.getChildrenGroupBeans().remove(bean);
					recursionRemove(parent, uid);
				}
			}
		}else{
			if(!bean.getGroupDescriptor().getOwner().equals(uid) && bean.getChildrenGroupBeans().isEmpty()){
				GroupBeanOld parent=bean.getParentGroupBean();
				if(parent!=null){
					parent.getChildrenGroupBeans().remove(bean);
					recursionRemove(parent, uid);
				}
			}
		}
	}
	@Override
	public GroupJobTreeModel getTreeData() {
		GroupBeanOld globe=null;
		globe=readOnlyGroupManager.getGlobeGroupBeanForTreeDisplay(false);
		return getTreeData(globe);
	}
	private GroupJobTreeModel getTreeData(GroupBeanOld rootGroup) {
		
		String uid=LoginUser.getUser().getUid();
		List<ZeusFollow> list=followManagerOld.findAllTypeFollows(uid);
		Map<String, Boolean> groupFollow=new HashMap<String, Boolean>();
		Map<String, Boolean> jobFollow=new HashMap<String, Boolean>();
		for(ZeusFollow f:list){
			if(ZeusFollow.GroupType.equals(f.getType())){
				groupFollow.put(f.getTargetId(), true);
			}else if(ZeusFollow.JobType.equals(f.getType())){
				jobFollow.put(f.getTargetId(), true);
			}
		}
		
		GroupJobTreeModel root=new GroupJobTreeModel();
		root.setName(rootGroup.getGroupDescriptor().getName());
		root.setId(rootGroup.getGroupDescriptor().getId());
		root.setGroup(true);
		root.setDirectory(true);
		root.setJob(false);
		root.setOwner(rootGroup.getGroupDescriptor().getOwner());
		Boolean isFollow=groupFollow.get(rootGroup.getGroupDescriptor().getId());
		root.setFollow(isFollow==null?false:isFollow);
		
		
		
		setGroup(root,rootGroup.getChildrenGroupBeans(),groupFollow,jobFollow);
		
		
		return root;
	}
	
	private void setGroup(GroupJobTreeModel parent,List<GroupBeanOld> children,Map<String, Boolean> groupFollow,Map<String, Boolean> jobFollow){
		Collections.sort(children, new Comparator<GroupBeanOld>() {
			public int compare(GroupBeanOld o1, GroupBeanOld o2) {
				return o1.getGroupDescriptor().getName().compareToIgnoreCase(o2.getGroupDescriptor().getName());
			}
		});
		for(GroupBeanOld g:children){
			if(g.isExisted()){
				GroupJobTreeModel group=new GroupJobTreeModel();
				group.setName(g.getGroupDescriptor().getName());
				group.setId(g.getGroupDescriptor().getId());
				group.setGroup(true);
				group.setJob(false);
				group.setOwner(g.getGroupDescriptor().getOwner());
				group.setDirectory(g.isDirectory());
				Boolean follow=groupFollow.get(g.getGroupDescriptor().getId());
				group.setFollow(follow==null?false:(follow?true:false));
				parent.getChildren().add(group);
				if(g.isDirectory()){
					setGroup(group,g.getChildrenGroupBeans(),groupFollow,jobFollow);
				}else{
					List<JobBeanOld> list=new ArrayList<JobBeanOld>();
					for(JobBeanOld jb:g.getJobBeans().values()){
						list.add(jb);
					}
					Collections.sort(list, new Comparator<JobBeanOld>() {
						public int compare(JobBeanOld o1, JobBeanOld o2) {
							return o1.getJobDescriptor().getName().compareTo(o2.getJobDescriptor().getName());
						}
					});
					for(JobBeanOld jb:list){
						GroupJobTreeModel job=new GroupJobTreeModel();
						job.setId(jb.getJobDescriptor().getId());
						job.setGroup(false);
						job.setDirectory(false);
						job.setName(jb.getJobDescriptor().getName());
						job.setJob(true);
						Boolean jFollow=jobFollow.get(job.getId());
						job.setFollow(jFollow==null?false:(jFollow?true:false));
						group.getChildren().add(job);
					}
				}
			}
		}
	}

	@Override
	public void follow(int type, String targetId) {
		followManagerOld.addFollow(LoginUser.getUser().getUid(), type, targetId);
	}

	@Override
	public void unfollow(int type, String targetId) {
		followManagerOld.deleteFollow(LoginUser.getUser().getUid(), type, targetId);
	}

	@Override
	public GroupJobTreeModel getDependeeTree(String jobId) {
		GroupBeanOld globe=readOnlyGroupManager.getGlobeGroupBean();
		JobBeanOld jb=globe.getAllSubJobBeans().get(jobId);
		if(jb!=null){
			GroupJobTreeModel root=new GroupJobTreeModel();
			root.setName(jb.getJobDescriptor().getName());
			root.setId(jb.getJobDescriptor().getId());
			root.setGroup(false);
			root.setDirectory(jb.getDependee().isEmpty()?false:true);
			root.setJob(true);
			root.setOwner(jb.getJobDescriptor().getOwner());
			
			setJob(root,jb.getDependee(),true);
			return root;
		}
		return null;
	}
	private void setJob(GroupJobTreeModel parent,Collection<JobBeanOld> children,boolean dependee){
		for(JobBeanOld g:children){
			GroupJobTreeModel job=new GroupJobTreeModel();
			job.setName(g.getJobDescriptor().getName());
			job.setId(g.getJobDescriptor().getId());
			job.setGroup(false);
			job.setJob(true);
			job.setOwner(g.getJobDescriptor().getOwner());
			Boolean dir=false;
			Collection<JobBeanOld> childs=null;
			if(dependee){
				dir=g.getDependee().isEmpty()?false:true;
				childs=g.getDependee();
			}else{
				dir=g.getDepender().isEmpty()?false:true;
				childs=g.getDepender();
			}
			job.setDirectory(dir);
			parent.getChildren().add(job);
			if(!childs.isEmpty()){
				setJob(job,childs,dependee);
			}
		}
	}

	@Override
	public GroupJobTreeModel getDependerTree(String jobId) {
		GroupBeanOld globe=readOnlyGroupManager.getGlobeGroupBean();
		JobBeanOld jb=globe.getAllSubJobBeans().get(jobId);
		if(jb!=null){
			GroupJobTreeModel root=new GroupJobTreeModel();
			root.setName(jb.getJobDescriptor().getName());
			root.setId(jb.getJobDescriptor().getId());
			root.setGroup(false);
			root.setDirectory(jb.getDepender().isEmpty()?false:true);
			root.setJob(true);
			root.setOwner(jb.getJobDescriptor().getOwner());
			
			setJob(root,jb.getDepender(),false);
			return root;
		}
		return null;
	}

	@Override
	public String getDependeeTreeJson(String jobId) {
		try {
			return getJsonData(getDependeeTree(jobId)).toString();
		} catch (Exception e) {
			log.error("getDependeeTreeJson",e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private JSONObject getJsonData(GroupJobTreeModel result) throws JSONException{
		JSONObject jsonData = new JSONObject();
		//JSONObject data = new JSONObject();
		jsonData.put("id", result.getId()+"-"+new Random().nextInt(1000));
		jsonData.put("name", result.getName());
		Map<String, JobHistory> map=jobHistoryManager.findLastHistoryByList(Arrays.asList(result.getId()));
		JobHistory his=map.get(result.getId());
		JSONObject data=new JSONObject();
		if(his==null){
			data.put("jobId",result.getId());
			data.put("historyId", "");
			data.put("lastStatus", "");
			data.put("lastRuntime", 0);
		}else{
			data.put("jobId",result.getId());
			data.put("historyId", his.getId());
			data.put("lastStatus", his.getStatus()==null?"":his.getStatus().toString());
			data.put("lastRuntime", his.getStartTime()==null?null:his.getStartTime().getTime());
		}
		jsonData.put("data", data);
		if(result.getChildren().size()>0)
			jsonData.put("children", getChildren(result));
		else
			jsonData.put("children", Collections.EMPTY_LIST);
		return jsonData;
	}
	private ArrayList<JSONObject> getChildren(GroupJobTreeModel result) throws JSONException{
		ArrayList<JSONObject> children = new ArrayList<JSONObject>();
		for(GroupJobTreeModel child : result.getChildren()){
			children.add(getJsonData(child));
		}
		return children;
	}
	@Override
	public String getDependerTreeJson(String jobId) {
		try {
			return getJsonData(getDependerTree(jobId)).toString();
		} catch (Exception e) {
			log.error("getDependerTreeJson",e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
}