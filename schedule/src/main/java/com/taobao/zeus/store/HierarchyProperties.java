package com.taobao.zeus.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 带层次结构的属性
 * @author zhoufang
 *
 */
public class HierarchyProperties {
	protected HierarchyProperties parent;
	protected Map<String, String> properties;
	
	public HierarchyProperties(Map<String, String> properties){
		this.properties=properties;
	}
	public HierarchyProperties(HierarchyProperties parent,Map<String, String> properties){
		this.parent=parent;
		if(properties==null){
			this.properties=new HashMap<String, String>();
		}else{
			this.properties=new HashMap<String, String>(properties);
		}
	}
	
	public Set<String> getPropertyKeys(){
		Set<String> set=new HashSet<String>();
		if(parent!=null){
			set.addAll(parent.getPropertyKeys());
		}
		for(Object key:properties.keySet()){
			set.add(key.toString());
		}
		return set;
	}
	
	/**
	 * 获取属性值
	 * 如果自身属性中没有，则向父属性查询
	 * @param key
	 * @return
	 */
	public String getProperty(String key){
		if(properties.get(key)!=null){
			return properties.get(key);
		}
		if(parent!=null){
			return parent.getProperty(key);
		}
		return null;
	}
	/**
	 * 获取属性值
	 * 如果自身属性中没有，则向父属性查询
	 * 如果没有，则返回传入的默认值
	 * @param key
	 * @return
	 */
	public String getProperty(String key,String defaultValue){
		String value=getProperty(key);
		if(value==null){
			value=defaultValue;
		}
		return value;
	}
	/**
	 * 获取属性值
	 * 只在自身属性中查询
	 * @param key
	 * @return
	 */
	public String getLocalProperty(String key){
		return properties.get(key);
	}
	/**
	 * 获取属性值
	 * 只在自身属性中查询
	 * 如果没有，则返回传入的默认值
	 * @param key
	 * @return
	 */
	public String getLocalProperty(String key,String defaultValue){
		String value=getLocalProperty(key);
		if(value==null){
			value=defaultValue;
		}
		return value;
	}
	/**
	 * 向上获取所有的数据
	 * 一般用于获取带继承性质的属性
	 * 比如classpath，需要父级的classpath
	 * @param key
	 * @return
	 */
	public List<String> getHierarchyProperty(String key){
		List<String> list=new ArrayList<String>();
		if(properties.get(key)!=null){
			list.add(properties.get(key));
		}
		if(parent!=null){
			list.addAll(parent.getHierarchyProperty(key));
		}
		return list;
	}
	public HierarchyProperties getParent() {
		return parent;
	}
	public Map<String, String> getLocalProperties() {
		return properties;
	}
	/**
	 * 获取所有的属性对，(包含继承属性，并且上级继承属性会被下级继承属性覆盖)
	 * @return
	 */
	public Map<String, String> getAllProperties(){
		if(parent!=null){
			Map<String, String> parentMap=new HashMap<String, String>(parent.getAllProperties());
			parentMap.putAll(getLocalProperties());
			return parentMap;
		}
		return getLocalProperties();
	}
	
	public void setProperty(String key,String value){
		properties.put(key, value);
	}
}
