package com.taobao.zeus.web.platform.client.module.tablemanager;

import com.taobao.zeus.web.platform.client.module.tablemanager.model.PartitionModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.util.Presenter;

public interface TableManagerPresenter extends Presenter {

	boolean isEnableDelete();

	void onDelete();

	void onSelect(TableModel fileModel);

	void loadDataPreview(PartitionModel selectedItem);

}
