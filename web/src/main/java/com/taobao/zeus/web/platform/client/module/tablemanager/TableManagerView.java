package com.taobao.zeus.web.platform.client.module.tablemanager;

import com.google.gwt.user.client.ui.IsWidget;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.PartitionModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;

public interface TableManagerView extends IsWidget {

	public TableModel getSelectedItem();

	public void selectFileModel(TableModel fileModel);

	public void loadDataPreview(PartitionModel selectedItem);

}
