package com.taobao.zeus.web.platform.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.PartitionModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.TablePreviewModel;

public interface TableManagerServiceAsync {

	void getPagingTables(FilterPagingLoadConfig loadConfigString, String uid, String dbName,
			AsyncCallback<PagingLoadResult<TableModel>> callback);

	void getTableModel(String dataBaseName, String tableName, AsyncCallback<TableModel> callback);

	void getPreviewData(PartitionModel model,
			AsyncCallback<TablePreviewModel> callback);

	void getPartitions(TableModel t,
			AsyncCallback<List<PartitionModel>> callback);

	void fillPartitionSize(PartitionModel p,
			AsyncCallback<PartitionModel> callback);

}
