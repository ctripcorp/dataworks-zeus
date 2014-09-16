package com.taobao.zeus.store;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.taobao.zeus.model.JobDescriptorOld;
import com.taobao.zeus.model.JobStatus;

/**
 * 内存处理模型，包含数结构,以及不同对象之间的引用关系
 * @author zhoufang
 */
public class JobBeanOld{
	private final JobDescriptorOld JobDescriptorOld;
	private final JobStatus jobStatus;
	private GroupBeanOld groupBean;

	private Set<JobBeanOld> dependee = new HashSet<JobBeanOld>();
	private Set<JobBeanOld> depender = new HashSet<JobBeanOld>();
	
	public JobBeanOld(JobDescriptorOld jd,JobStatus jobStatus){
		this.JobDescriptorOld=jd;
		this.jobStatus=jobStatus;
	}
	/**
	 * 获取任务被依赖的集合
	 * @return
	 */
	public Set<JobBeanOld> getDepender(){
		return depender;
	}
	/**
	 * 获取任务的依赖集合
	 * @return
	 */
	public Set<JobBeanOld> getDependee(){
		return dependee;
	}
	/**
	 * 获取带层次的属性
	 * @return
	 */
	public HierarchyProperties getHierarchyProperties(){
		if(groupBean!=null){
			return new HierarchyProperties(groupBean.getHierarchyProperties(), JobDescriptorOld.getProperties());
		}
		return new HierarchyProperties(JobDescriptorOld.getProperties());
	}
	
	public List<Map<String, String>> getHierarchyResources(){
		List<String> existList=new ArrayList<String>();
		List<Map<String, String>> local=new ArrayList<Map<String,String>>(JobDescriptorOld.getResources());
		if(local==null){
			local=new ArrayList<Map<String,String>>();
		}
		for(Map<String, String> map:local){
			if(map.get("name")!=null && !existList.contains(map.get("name"))){
				existList.add(map.get("name"));
			}
		}
		if(groupBean!=null){
			List<Map<String, String>> parent=groupBean.getHierarchyResources();
			for(Map<String, String> map:parent){
				if(map.get("name")!=null && !existList.contains(map.get("name"))){
					existList.add(map.get("name"));
					local.add(map);
				}
			}
		}
		return local;
	}
	/**
	 * 添加依赖的任务
	 * 内存操作，不做持久化处理!
	 * @param dep
	 */
	public void addDependee(JobBeanOld dep){
		if(!dependee.contains(dep)){
			dependee.add(dep);
		}
	}
	/**
	 * 添加被依赖的任务
	 * 内存操作，不做持久化处理!
	 * @param dep
	 */
	public void addDepender(JobBeanOld dep){
		if(!depender.contains(dep)){
			depender.add(dep);
		}
	}
	/**
	 * 获取Job所属的Group
	 * @return
	 */
	public GroupBeanOld getGroupBean(){
		return groupBean;
	}
	
	public JobDescriptorOld getJobDescriptor(){
		return JobDescriptorOld;
	}
	public void setGroupBean(GroupBeanOld groupBean) {
		this.groupBean = groupBean;
	}
	public JobStatus getJobStatus() {
		return jobStatus;
	}
	
}
