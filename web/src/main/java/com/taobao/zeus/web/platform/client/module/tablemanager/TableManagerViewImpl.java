package com.taobao.zeus.web.platform.client.module.tablemanager;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.taobao.zeus.web.login_out;
import com.taobao.zeus.web.platform.client.module.tablemanager.component.DataPreviewGrid;
import com.taobao.zeus.web.platform.client.module.tablemanager.component.TableInfoPanel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.PartitionModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModelProperties;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.widget.MyStringFilter;
import com.taobao.zeus.web.platform.shared.rpc.TableManagerServiceAsync;

public class TableManagerViewImpl implements TableManagerView {

	private TableManagerPresenter tableManagerPresenter;
	@SuppressWarnings("unused")
	private PlatformContext context;
	private ListStore<TableModel> commonStore;
	private final TableModelProperties props = GWT
			.create(TableModelProperties.class);

	private BorderLayoutContainer container;
	@UiField
	ContentPanel commonTab;
	@UiField
	VerticalLayoutContainer commonTableContainer;
	@UiField
	ContentPanel dataPreviewPanel;
	private Grid<TableModel> commonGrid;
	private TableInfoPanel commonTableInfoPanel;
	private final TextField dbNameText;
	private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<TableModel>> loader;
	private static TableManagerViewImplUiBinder uiBinder = GWT
			.create(TableManagerViewImplUiBinder.class);

	interface TableManagerViewImplUiBinder extends
			UiBinder<BorderLayoutContainer, TableManagerViewImpl> {
	}

	private final TableManagerServiceAsync tableService = RPCS
			.getTableManagerService();

	public TableManagerViewImpl(PlatformContext context,
			TableManagerPresenter presenter) {
		this.tableManagerPresenter = presenter;
		this.context = context;
		dbNameText = new TextField();
		dbNameText.setWidth(280);
		dbNameText.setEmptyText("数据库名称");
		container = uiBinder.createAndBindUi(this);

		commonTableContainer.add(getCommonGrid(), new VerticalLayoutData(1, 1));
		commonTableContainer.add(getToolbar(), new VerticalLayoutData(1, 30));
		commonTableContainer.add(getTableInfoPanel(), new VerticalLayoutData(1,
				220, new Margins(1)));
	}

	/*@UiFactory
	public ContentPanel createContentPanel(String headingText) {
		ContentPanel cp;
		// 数据预览窗口第一次渲染后缩起
		if (headingText.equals("数据预览")) {
			cp = new ContentPanel() {
				@Override
				protected void onAfterFirstAttach() {
					container.collapse(LayoutRegion.SOUTH);
				}
			};
		} else {
			cp = new ContentPanel();
		}

		cp.setHeadingText(headingText);
		return cp;
	}*/

	@Override
	public TableModel getSelectedItem() {
		return getCommonGrid().getSelectionModel().getSelectedItem();
	}

	@Override
	public void selectFileModel(TableModel fileModel) {
		getCommonGrid().getSelectionModel().select(fileModel, false);
	}

	@Override
	public Widget asWidget() {
		return container;
	}

	public TableManagerPresenter getFileManagerPresenter() {
		return tableManagerPresenter;
	}

	public Grid<TableModel> getCommonGrid() {
		if (commonGrid == null) {
			ColumnConfig<TableModel, String> name = new ColumnConfig<TableModel, String>(
					props.name(), 280, "Name");
			name.setSortable(false);
			List<ColumnConfig<TableModel, ?>> list = new ArrayList<ColumnConfig<TableModel, ?>>();
			list.add(name);
			ColumnModel<TableModel> cm = new ColumnModel<TableModel>(list);

			commonGrid = new Grid<TableModel>(getCommonStore(), cm);
			commonGrid.getView().setForceFit(true);
			commonGrid.setAllowTextSelection(true);
			commonGrid.setLoader(getLoader());
			commonGrid.setHideHeaders(true);
			commonGrid.setLoadMask(true);
			commonGrid.getView().setForceFit(true);
			commonGrid.getView().setAutoFill(true);
			commonGrid.getView().setEmptyText("没有查询结果！");

			final GridFilters<TableModel> filters = new GridFilters<TableModel>(
					loader);
			filters.initPlugin(commonGrid);
			final MyStringFilter<TableModel> myFilter = new MyStringFilter<TableModel>(
					props.name(), getLoader()) {
				@Override
				protected void onFieldKeyUp(Event event) {
					if (field.getCurrentValue() == null
							|| field.getCurrentValue().trim().length() < 3)
						return;
					super.onFieldKeyUp(event);
				}
			};
			filters.addFilter(myFilter);
			final TextField filterText = myFilter.getField();
			filterText.setWidth(280);
			filterText.setEmptyText("表名，关键词用空格隔开,'*'代表任意一个或多个字符");

			commonTableContainer.add(dbNameText, new VerticalLayoutData(1, 30,
					new Margins(3)));
			commonTableContainer.add(filterText, new VerticalLayoutData(1, 30,
					new Margins(3)));
			GridSelectionModel<TableModel> gs = new GridSelectionModel<TableModel>();
			gs.addSelectionHandler(new SelectionHandler<TableModel>() {
				@Override
				public void onSelection(SelectionEvent<TableModel> event) {
					getTableInfoPanel().load(event.getSelectedItem());
				}
			});
			commonGrid.setSelectionModel(gs);
		}
		return commonGrid;
	}

	public PagingToolBar getToolbar() {

		final PagingToolBar toolBar = new PagingToolBar(30) {
			@Override
			protected void onAfterFirstAttach() {
				displayText.hide();
				super.onAfterFirstAttach();
			}
		};
		toolBar.bind(getLoader());

		return toolBar;
	}

	private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<TableModel>> getLoader() {
		if (loader == null) {
			RpcProxy<FilterPagingLoadConfig, PagingLoadResult<TableModel>> proxy = new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<TableModel>>() {
				@Override
				public void load(FilterPagingLoadConfig loadConfig,
						AsyncCallback<PagingLoadResult<TableModel>> callback) {
					String dbname = dbNameText.getValue();
					tableService.getPagingTables(loadConfig, null, dbname, callback);
				}
			};
			loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<TableModel>>(
					proxy) {
				@Override
				protected FilterPagingLoadConfig newLoadConfig() {
					return new FilterPagingLoadConfigBean();
				}
			};
			loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, TableModel, PagingLoadResult<TableModel>>(
					getCommonStore()));

			loader.setRemoteSort(true);

			loader.addLoadHandler(new LoadHandler<FilterPagingLoadConfig, PagingLoadResult<TableModel>>() {

				@Override
				public void onLoad(
						LoadEvent<FilterPagingLoadConfig, PagingLoadResult<TableModel>> event) {
					if (event.getLoadResult().getData().isEmpty())
						return;
					getTableInfoPanel().load(
							event.getLoadResult().getData().get(0));
				}

			});
		}
		return this.loader;
	}

	public ListStore<TableModel> getCommonStore() {
		if (commonStore == null) {
			commonStore = new ListStore<TableModel>(
					new ModelKeyProvider<TableModel>() {
						@Override
						public String getKey(TableModel item) {
							return item.getName();
						}
					});
		}

		return commonStore;
	}

	private TableInfoPanel getTableInfoPanel() {
		if (commonTableInfoPanel == null) {
			commonTableInfoPanel = new TableInfoPanel(tableManagerPresenter);
			commonTableInfoPanel.setBorders(false);
		}
		return this.commonTableInfoPanel;
	}

	@Override
	public void loadDataPreview(PartitionModel pm) {
		if (pm != null) {
//			container.expand(LayoutRegion.SOUTH);
			dataPreviewPanel.mask("Loading...");
			tableService.getPreviewData(pm,
					new AbstractAsyncCallback<TablePreviewModel>() {

						@Override
						public void onSuccess(TablePreviewModel result) {
							dataPreviewPanel.clear();
							dataPreviewPanel.add(new DataPreviewGrid(result));
							dataPreviewPanel.unmask();
						}

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							dataPreviewPanel.unmask();
						}
					});
		}

	}
}
