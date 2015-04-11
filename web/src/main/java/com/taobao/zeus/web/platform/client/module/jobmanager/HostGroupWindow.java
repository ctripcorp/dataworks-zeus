package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.HostGroupProperties;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.HostGroupModel;

public class HostGroupWindow extends Window {
	private ListStore<HostGroupModel> store;
	private Grid<HostGroupModel> grid;
	private PagingToolBar toolBar;
	private PagingLoader<PagingLoadConfig, PagingLoadResult<HostGroupModel>> loader;
	private final HostGroupProperties props = GWT
			.create(HostGroupProperties.class);
	private VerticalLayoutContainer container;
	private SelectHandler handler;

	public void setSelectHandler(SelectHandler handler) {
		this.handler = handler;
	}

	public HostGroupWindow() {
		setHeadingText("host分组信息");
		setModal(true);
		setHeight(600);
		setWidth(500);
		container = new VerticalLayoutContainer();
		container.setHeight(300);
		container.setWidth(300);
		add(container);
		ColumnConfig<HostGroupModel, String> idColumn = new ColumnConfig<HostGroupModel, String>(
				props.id(), 30, "id");
		ColumnConfig<HostGroupModel, String> nameColumn = new ColumnConfig<HostGroupModel, String>(
				props.name(), 30, "组名");
		ColumnConfig<HostGroupModel, String> descriptionColumn = new ColumnConfig<HostGroupModel, String>(
				props.description(), 60, "描述");
		ColumnModel<HostGroupModel> cm = new ColumnModel(Arrays.asList(
				idColumn, nameColumn, descriptionColumn));

		store = new ListStore<HostGroupModel>(
				new ModelKeyProvider<HostGroupModel>() {
					@Override
					public String getKey(HostGroupModel item) {
						return String.valueOf(item.getId());
					}
				});

		RpcProxy<PagingLoadConfig, PagingLoadResult<HostGroupModel>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<HostGroupModel>>() {

			@Override
			public void load(
					PagingLoadConfig loadConfig,
					final AsyncCallback<PagingLoadResult<HostGroupModel>> callback) {
				RPCS.getJobService().getHostGroup(loadConfig, callback);
			}
		};

		loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<HostGroupModel>>(
				proxy);
		loader.setLimit(20);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, HostGroupModel, PagingLoadResult<HostGroupModel>>(
				store));
		grid = new Grid<HostGroupModel>(store, cm);
		grid.setLoader(loader);
		grid.setLoadMask(true);
		grid.getView().setForceFit(true);

		toolBar = new PagingToolBar(20);
		toolBar.bind(loader);

		container.add(grid,new VerticalLayoutData(1, 1));
		container.add(toolBar,new VerticalLayoutData(1, 30));
		
		addButton(new TextButton("确定", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if (handler != null) {
					handler.onSelect(event);
				}
			}
		}));
		refresh();
	}

	public Grid<HostGroupModel> getGrid() {
		return grid;
	}
	public void refresh() {
		PagingLoadConfig config = new PagingLoadConfigBean();
		config.setOffset(0);
		config.setLimit(20);
		loader.load(config);
	}
}