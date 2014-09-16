package com.taobao.zeus.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.zeus.model.GroupDescriptor;

public class GroupBeanOld implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private GroupBeanOld parentGroupBean;
	
	private final GroupDescriptor groupDescriptor;
	

	private Map<String, JobBeanOld> jobBeanMap=new HashMap<String, JobBeanOld>();
	
	private List<GroupBeanOld> children=new ArrayList<GroupBeanOld>();
	
	public GroupBeanOld(GroupDescriptor g){
		this.groupDescriptor=g;
	}
	/**
	 * 获取带层次的属性
	 * @return
	 */
	public HierarchyProperties getHierarchyProperties(){
		if(parentGroupBean!=null){
			return new HierarchyProperties(parentGroupBean.getHierarchyProperties(), groupDescriptor.getProperties());
		}
		return new HierarchyProperties(groupDescriptor.getProperties());
	}
	
	public Map<String, String> getProperties(){
		return groupDescriptor.getProperties();
	}
	
	public List<Map<String, String>> getHierarchyResources(){
		List<Map<String, String>> local=new ArrayList<Map<String,String>>(groupDescriptor.getResources());
		if(local==null){
			local=new ArrayList<Map<String,String>>();
		}
		if(parentGroupBean!=null){
			local.addAll(parentGroupBean.getHierarchyResources());
		}
		return local;
	}
	/**
	 * 获取所有组下组(无限级)
	 * @return
	 */
	public Map<String, GroupBeanOld> getAllSubGroupBeans(){
		Map<String, GroupBeanOld> map=new HashMap<String, GroupBeanOld>();
		for(GroupBeanOld gb:getChildrenGroupBeans()){
			for(GroupBeanOld child:getChildrenGroupBeans()){
				map.put(child.getGroupDescriptor().getId(), child);
			}
			map.putAll(gb.getAllSubGroupBeans());
		}
		return map;
	}
	/**
	 * 获取组下(无限级)所有的任务Map
	 * @return
	 */
	public Map<String, JobBeanOld> getAllSubJobBeans(){
		Map<String, JobBeanOld> map=new HashMap<String, JobBeanOld>();
		for(GroupBeanOld gb:getChildrenGroupBeans()){
			map.putAll(gb.getAllSubJobBeans());
		}
		map.putAll(jobBeanMap);
		return map;
	}
	/**
	 * 获取组下(一级)所有的任务Map
	 * @return
	 */
	public Map<String, JobBeanOld> getJobBeans(){
		return jobBeanMap;
	}
	/**
	 * 获取JobBean
	 * @param jobId
	 * @return
	 */
	public JobBeanOld getJobBean(String jobId){
		return jobBeanMap.get(jobId);
	}
	/**
	 * 获取父级GroupBean
	 * @return
	 */
	public GroupBeanOld getParentGroupBean(){
		return parentGroupBean;
	}
	/**
	 * 获取下一级GroupBean
	 * @return
	 */
	public List<GroupBeanOld> getChildrenGroupBeans(){
		return children;
	}
	
	public GroupDescriptor getGroupDescriptor(){
		return groupDescriptor;
	}
	
	public boolean isDirectory(){
		return groupDescriptor.isDirectory();
	}
	public void setParentGroupBean(GroupBeanOld parentGroupBean) {
		this.parentGroupBean = parentGroupBean;
	}
}
