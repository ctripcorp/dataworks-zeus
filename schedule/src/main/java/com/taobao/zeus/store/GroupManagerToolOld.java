package com.taobao.zeus.store;

import java.util.List;
import java.util.Map;






import org.antlr.grammar.v3.ANTLRParser.id_return;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore.Processor.isPartitionMarkedForEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.zeus.model.GroupDescriptor;
import com.taobao.zeus.model.JobDescriptorOld;
import com.taobao.zeus.model.JobStatus;
import com.taobao.zeus.model.JobDescriptorOld.JobScheduleTypeOld;
import com.taobao.zeus.util.Tuple;

public class GroupManagerToolOld {
	private static Logger log = LoggerFactory.getLogger(GroupManagerToolOld.class);
	
	public static GroupBeanOld getUpstreamGroupBean(String groupId,GroupManagerOld groupManagerOld) {
		GroupDescriptor group=groupManagerOld.getGroupDescriptor(groupId);
		GroupBeanOld result=new GroupBeanOld(group);
		if(group.getParent()!=null){
			GroupBeanOld parent=groupManagerOld.getUpstreamGroupBean(group.getParent());
			result.setParentGroupBean(parent);
		}
		return result;
	}
	/**
	 * 构建一个带完整依赖关系的树形节点网络
	 * @param GroupManagerOld
	 * @return
	 */
	public static GroupBeanOld buildGlobeGroupBean(GroupManagerOld groupManagerOld) {
		GroupBeanOld root=groupManagerOld.getDownstreamGroupBean(groupManagerOld.getRootGroupId());
		//构建依赖关系的网状结构
		//1.提取所有的GroupBean 和 JobBeanOld
//		structureDependNet(root, root.getAllGroupBeanMap(), root.getAllJobBeanMap());
		//2.将JobBean中的依赖关系在内存模型中关联起来
		Map<String, JobBeanOld> allJobBeans=root.getAllSubJobBeans();
		for(JobBeanOld j1:allJobBeans.values()){
			if(j1.getJobDescriptor().getScheduleType()==JobScheduleTypeOld.Dependent){
				for(String depId:j1.getJobDescriptor().getDependencies()){
					try {
						JobBeanOld depJob=allJobBeans.get(depId);
						j1.addDependee(depJob);
						depJob.addDepender(j1);
					} catch (Exception e) {
						log.error("The id is " + j1.getJobDescriptor().getId() + ", the depId is " + depId);
					}
					
				}
			}
		}
		return root;
	}
	/**
	 * 构建一个树形节点网络，不包含Job之间的依赖关系对象引用
	 * @param GroupManagerOld
	 * @return
	 */
	public static GroupBeanOld buildGlobeGroupBeanWithoutDepend(GroupManagerOld groupManagerOld) {
		GroupBeanOld root=groupManagerOld.getDownstreamGroupBean(groupManagerOld.getRootGroupId());
		return root;
	}
	public static GroupBeanOld getDownstreamGroupBean(String groupId,GroupManagerOld groupManagerOld) {
		GroupDescriptor group=groupManagerOld.getGroupDescriptor(groupId);
		GroupBeanOld result=new GroupBeanOld(group);
		return groupManagerOld.getDownstreamGroupBean(result);
	}
	
	public static GroupBeanOld getDownstreamGroupBean(GroupBeanOld parent,GroupManagerOld groupManagerOld) {
		if(parent.isDirectory()){
			List<GroupDescriptor> children=groupManagerOld.getChildrenGroup(parent.getGroupDescriptor().getId());
			for(GroupDescriptor child:children){
				GroupBeanOld childBean=new GroupBeanOld(child);
				groupManagerOld.getDownstreamGroupBean(childBean);
				childBean.setParentGroupBean(parent);
				parent.getChildrenGroupBeans().add(childBean);
			}
		}else{
			List<Tuple<JobDescriptorOld, JobStatus>> jobs=groupManagerOld.getChildrenJob(parent.getGroupDescriptor().getId());
			for(Tuple<JobDescriptorOld, JobStatus> tuple:jobs){
				JobBeanOld JobBeanOld=new JobBeanOld(tuple.getX(),tuple.getY());
				JobBeanOld.setGroupBean(parent);
				parent.getJobBeans().put(tuple.getX().getId(), JobBeanOld);
			}
		}
		
		return parent;
	}
	
	public static JobBeanOld getUpstreamJobBean(String jobId,GroupManagerOld groupManagerOld) {
		Tuple<JobDescriptorOld, JobStatus> tuple=groupManagerOld.getJobDescriptor(jobId);
		if(tuple!=null){
			JobBeanOld result=new JobBeanOld(tuple.getX(),tuple.getY());
			result.setGroupBean(groupManagerOld.getUpstreamGroupBean(result.getJobDescriptor().getGroupId()));
			return result;
		}else{
			return null;
		}
	}
}
