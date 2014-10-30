package com.taobao.zeus.store;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.taobao.zeus.model.JobDescriptor;
import com.taobao.zeus.model.JobStatus;

/**
 * 内存处理模型，包含数结构,以及不同对象之间的引用关系
 * @author zhoufang
 */
public class JobBean{
	private final JobDescriptor jobDescriptor;
	private final JobStatus jobStatus;
	private GroupBean groupBean;

	private Set<JobBean> dependee = new HashSet<JobBean>();
	private Set<JobBean> depender = new HashSet<JobBean>();
	
	public JobBean(JobDescriptor jd,JobStatus jobStatus){
		this.jobDescriptor=jd;
		this.jobStatus=jobStatus;
	}
	/**
	 * 获取任务被依赖的集合
	 * @return
	 */
	public Set<JobBean> getDepender(){
		return depender;
	}
	/**
	 * 获取任务的依赖集合
	 * @return
	 */
	public Set<JobBean> getDependee(){
		return dependee;
	}
	/**
	 * 获取带层次的属性
	 * @return
	 */
	public HierarchyProperties getHierarchyProperties(){
		if(groupBean!=null){
			return new HierarchyProperties(groupBean.getHierarchyProperties(), jobDescriptor.getProperties());
		}
		return new HierarchyProperties(jobDescriptor.getProperties());
	}
	
	public List<Map<String, String>> getHierarchyResources(){
		List<String> existList=new ArrayList<String>();
		List<Map<String, String>> local=new ArrayList<Map<String,String>>(jobDescriptor.getResources());
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
	public void addDependee(JobBean dep){
		if(!dependee.contains(dep)){
			dependee.add(dep);
		}
	}
	/**
	 * 添加被依赖的任务
	 * 内存操作，不做持久化处理!
	 * @param dep
	 */
	public void addDepender(JobBean dep){
		if(!depender.contains(dep)){
			depender.add(dep);
		}
	}
	/**
	 * 获取Job所属的Group
	 * @return
	 */
	public GroupBean getGroupBean(){
		return groupBean;
	}
	
	public JobDescriptor getJobDescriptor(){
		return jobDescriptor;
	}
	public void setGroupBean(GroupBean groupBean) {
		this.groupBean = groupBean;
	}
	public JobStatus getJobStatus() {
		return jobStatus;
	}
	
}
