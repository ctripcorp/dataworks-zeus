package com.taobao.zeus.store;

import java.util.List;
import java.util.Map;

import com.taobao.zeus.model.HostGroupCache;
import com.taobao.zeus.store.mysql.persistence.HostGroupPersistence;

public interface HostGroupManager{
	public HostGroupPersistence getHostGroupName(String hostGroupId);
	
	public Map<String,HostGroupCache> getAllHostGroupInfomations();
	
	public List<HostGroupPersistence> getAllHostGroup();
	
	public List<String> getPreemptionHost();
}
