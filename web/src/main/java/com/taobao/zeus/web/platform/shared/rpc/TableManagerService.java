package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.PartitionModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.TablePreviewModel;
import com.taobao.zeus.web.platform.client.util.GwtException;

/**
 * hive表管理服务
 * 
 * @author gufei.wzy 2012-9-17
 */
@RemoteServiceRelativePath("table.rpc")
public interface TableManagerService extends RemoteService {

	/**
	 * 获取一张hive表的model
	 * 
	 * @param tableName
	 * @return
	 */
	TableModel getTableModel(String tableName);

	/**
	 * 分页获取hive表的model列表
	 * 
	 * @param loadConfigString
	 * @param uid
	 * @return
	 * @throws GwtException
	 */
	PagingLoadResult<TableModel> getPagingTables(
			FilterPagingLoadConfig loadConfigString, String uid)
			throws GwtException;

	/**
	 * 获取预览数据
	 * 
	 * @param tableName
	 * @param part
	 * @throws GwtException
	 */
	TablePreviewModel getPreviewData(PartitionModel model) throws GwtException;

	public List<PartitionModel> getPartitions(TableModel t) throws GwtException;

	public PartitionModel fillPartitionSize(PartitionModel p);
}
