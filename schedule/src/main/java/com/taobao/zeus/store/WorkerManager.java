package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.model.WorkerGroupCache;
import com.taobao.zeus.store.mysql.persistence.WorkerGroupPersistence;

public interface WorkerManager{
	public WorkerGroupPersistence getWorkerGroupName(String workerGroupId);
	
	public List<WorkerGroupCache> getAllWorkerGroupInfomations();
	
	public List<WorkerGroupPersistence> getAllWorkerGroup();
	
	public List<String> getDefaultMasterHost();
}
