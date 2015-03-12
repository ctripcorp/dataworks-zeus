package com.taobao.zeus.store;

import java.util.List;

import com.taobao.zeus.model.WorkerGroupCache;
import com.taobao.zeus.store.mysql.persistence.WorkerGroupPersistence;
import com.taobao.zeus.store.mysql.persistence.WorkerRelationPersistence;

public interface WorkerManager{
	public List<WorkerRelationPersistence> getAllWorkerRelations();
	
	public List<WorkerGroupCache> getAllWorkerGroupInfomations();
	
	public List<WorkerGroupPersistence> getAllWorkerGroup();
}
