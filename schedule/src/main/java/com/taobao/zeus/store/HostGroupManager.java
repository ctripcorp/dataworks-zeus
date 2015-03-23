package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.model.HostGroupCache;
import com.taobao.zeus.store.mysql.persistence.HostGroupPersistence;

public interface HostGroupManager{
	public HostGroupPersistence getHostGroupName(String hostGroupId);
	
	public List<HostGroupCache> getAllHostGroupInfomations();
	
	public List<HostGroupPersistence> getAllHostGroup();
	
	public List<String> getPreemptionHost();
}
