package com.taobao.zeus.web.platform.client.module.tablemanager;

import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.PartitionModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

public class TableManagerPresenterImpl implements TableManagerPresenter {

	private TableManagerView tableManagerView;
	private PlatformContext context;
	private PartitionModel loadedDataPreviewPartition;

	public TableManagerPresenterImpl(PlatformContext context) {
		this.context = context;
	}

	@Override
	public boolean isEnableDelete() {
		TableModel selectedItem = getTableManagerView().getSelectedItem();
		if (selectedItem == null) {
			return false;
		}
		return true;
	}

	@Override
	public void onDelete() {
		// List<TableModel> fileModels =
		// getFileManagerView().getSelectedItems();

	}

	@Override
	public void onSelect(TableModel fileModel) {
		// getDesktopBus().fireSelectFileModelEvent(new
		// SelectFileModelEvent(fileModel));
	}

	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(getTableManagerView().asWidget());
	}

	private TableManagerView getTableManagerView() {
		if (tableManagerView == null) {
			tableManagerView = new TableManagerViewImpl(context, this);
		}
		return tableManagerView;
	}

	@Override
	public PlatformContext getPlatformContext() {
		return this.context;
	}

	@Override
	public void loadDataPreview(PartitionModel selectedItem) {
		if (loadedDataPreviewPartition == selectedItem)
			return;
		getTableManagerView().loadDataPreview(selectedItem);
		loadedDataPreviewPartition = selectedItem;
	}
}
