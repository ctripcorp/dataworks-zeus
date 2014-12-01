package com.taobao.zeus.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.zeus.model.GroupDescriptor;

public class GroupBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private GroupBean parentGroupBean;
	
	private final GroupDescriptor groupDescriptor;
	

	private Map<String, JobBean> jobBeanMap=new HashMap<String, JobBean>();
	
	private List<GroupBean> children=new ArrayList<GroupBean>();
	
	public GroupBean(GroupDescriptor g){
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
	public Map<String, GroupBean> getAllSubGroupBeans(){
		Map<String, GroupBean> map=new HashMap<String, GroupBean>();
		for(GroupBean gb:getChildrenGroupBeans()){
			for(GroupBean child:getChildrenGroupBeans()){
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
	public Map<String, JobBean> getAllSubJobBeans(){
		Map<String, JobBean> map=new HashMap<String, JobBean>();
		for(GroupBean gb:getChildrenGroupBeans()){
			map.putAll(gb.getAllSubJobBeans());
		}
		map.putAll(jobBeanMap);
		return map;
	}
	/**
	 * 获取组下(一级)所有的任务Map
	 * @return
	 */
	public Map<String, JobBean> getJobBeans(){
		return jobBeanMap;
	}
	/**
	 * 获取JobBean
	 * @param jobId
	 * @return
	 */
	public JobBean getJobBean(String jobId){
		return jobBeanMap.get(jobId);
	}
	/**
	 * 获取父级GroupBean
	 * @return
	 */
	public GroupBean getParentGroupBean(){
		return parentGroupBean;
	}
	/**
	 * 获取下一级GroupBean
	 * @return
	 */
	public List<GroupBean> getChildrenGroupBeans(){
		return children;
	}
	
	public GroupDescriptor getGroupDescriptor(){
		return groupDescriptor;
	}
	
	public boolean isDirectory(){
		return groupDescriptor.isDirectory();
	}
	public void setParentGroupBean(GroupBean parentGroupBean) {
		this.parentGroupBean = parentGroupBean;
	}
	public boolean isExisted(){
		return groupDescriptor.isExisted();
	}
}
