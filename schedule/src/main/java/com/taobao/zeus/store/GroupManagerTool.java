package com.taobao.zeus.store;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.zeus.model.GroupDescriptor;
import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobDescriptor.JobScheduleType;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.socket.master.Master;
import com.taobao.zeus.util.Tuple;

public class GroupManagerTool {

	private static Logger log = LoggerFactory.getLogger(GroupManagerTool.class);

	public static GroupBean getUpstreamGroupBean(String groupId,GroupManager groupManager) {
		GroupDescriptor group=groupManager.getGroupDescriptor(groupId);
		GroupBean result=new GroupBean(group);
		if(group.getParent()!=null){
			GroupBean parent=groupManager.getUpstreamGroupBean(group.getParent());
			result.setParentGroupBean(parent);
		}
		return result;
	}
	/**
	 * 构建一个带完整依赖关系的树形节点网络
	 * @param groupManager
	 * @return
	 */
	public static GroupBean buildGlobeGroupBean(GroupManager groupManager) {
		GroupBean root=groupManager.getDownstreamGroupBean(groupManager.getRootGroupId());
		//构建依赖关系的网状结构
		//1.提取所有的GroupBean 和 JobBean
//		structureDependNet(root, root.getAllGroupBeanMap(), root.getAllJobBeanMap());
		//2.将JobBean中的依赖关系在内存模型中关联起来
		Map<String, JobBean> allJobBeans=root.getAllSubJobBeans();
		for(JobBean j1:allJobBeans.values()){
			if(j1.getJobDescriptor().getScheduleType()==JobScheduleType.Dependent){
				for(String depId:j1.getJobDescriptor().getDependencies()){
					try{
						JobBean depJob=allJobBeans.get(depId);
						j1.addDependee(depJob);
						depJob.addDepender(j1);
					}catch(Exception e){
						log.error("the jobid is" + j1.getJobDescriptor().getId() + ", the depId is " + depId);
					}
				}
			}
		}
		return root;
	}
	/**
	 * 构建一个树形节点网络，不包含Job之间的依赖关系对象引用
	 * @param groupManager
	 * @return
	 */
	public static GroupBean buildGlobeGroupBeanWithoutDepend(GroupManager groupManager) {
		GroupBean root=groupManager.getDownstreamGroupBean(groupManager.getRootGroupId());
		return root;
	}
	public static GroupBean getDownstreamGroupBean(String groupId,GroupManager groupManager) {
		GroupDescriptor group=groupManager.getGroupDescriptor(groupId);
		GroupBean result=new GroupBean(group);
		return groupManager.getDownstreamGroupBean(result);
	}
	
	public static GroupBean getDownstreamGroupBean(GroupBean parent,GroupManager groupManager) {
		if(parent.isDirectory()){
			List<GroupDescriptor> children=groupManager.getChildrenGroup(parent.getGroupDescriptor().getId());
			for(GroupDescriptor child:children){
				GroupBean childBean=new GroupBean(child);
				groupManager.getDownstreamGroupBean(childBean);
				childBean.setParentGroupBean(parent);
				parent.getChildrenGroupBeans().add(childBean);
			}
		}else{
			List<Tuple<JobDescriptor, JobStatus>> jobs=groupManager.getChildrenJob(parent.getGroupDescriptor().getId());
			for(Tuple<JobDescriptor, JobStatus> tuple:jobs){
				JobBean jobBean=new JobBean(tuple.getX(),tuple.getY());
				jobBean.setGroupBean(parent);
				parent.getJobBeans().put(tuple.getX().getId(), jobBean);
			}
		}
		
		return parent;
	}
	
	public static JobBean getUpstreamJobBean(String jobId,GroupManager groupManager) {
		Tuple<JobDescriptor, JobStatus> tuple=groupManager.getJobDescriptor(jobId);
		if(tuple!=null){
			JobBean result=new JobBean(tuple.getX(),tuple.getY());
			result.setGroupBean(groupManager.getUpstreamGroupBean(result.getJobDescriptor().getGroupId()));
			return result;
		}else{
			return null;
		}
	}
}
