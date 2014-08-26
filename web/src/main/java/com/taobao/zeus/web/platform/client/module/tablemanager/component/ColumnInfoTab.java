/**
 * 
 */
package com.taobao.zeus.web.platform.client.module.tablemanager.component;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableColumnModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableColumnModelProperties;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.widget.TitledCell;

/**
 * @author gufei.wzy 2012-9-21
 */
public class ColumnInfoTab implements IsWidget {
	@SuppressWarnings("unused")
	private PlatformContext context;
	private ListStore<TableColumnModel> store;

	private final TableColumnModelProperties props = GWT
			.create(TableColumnModelProperties.class);

	public ColumnInfoTab(TableInfoPanel p) {
		this.context = p.getContext();
	}

	@Override
	public Widget asWidget() {
		ColumnConfig<TableColumnModel, String> name = new ColumnConfig<TableColumnModel, String>(
				props.name(), 100, "Name");
		name.setCell(new TitledCell());
		ColumnConfig<TableColumnModel, String> type = new ColumnConfig<TableColumnModel, String>(
				props.type(), 50, "Type");
		type.setCell(new TitledCell());
		ColumnConfig<TableColumnModel, String> desc = new ColumnConfig<TableColumnModel, String>(
				props.desc(), 130, "Comment");
		desc.setCell(new TitledCell());
		List<ColumnConfig<TableColumnModel, ?>> list = new ArrayList<ColumnConfig<TableColumnModel, ?>>();
		list.add(name);
		list.add(type);
		list.add(desc);
		ColumnModel<TableColumnModel> colModel = new ColumnModel<TableColumnModel>(
				list);
		Grid<TableColumnModel> grid = new Grid<TableColumnModel>(getStore(),
				colModel);
		grid.setAllowTextSelection(true);

		grid.getView().setForceFit(true);
		grid.getView().setAutoFill(true);

		return grid;
	}

	public void load(TableModel t) {
		getStore().clear();
		if (t.getCols() != null) {
			getStore().addAll(t.getCols());
		}
	}

	private ListStore<TableColumnModel> getStore() {
		if (store == null) {
			store = new ListStore<TableColumnModel>(
					new ModelKeyProvider<TableColumnModel>() {
						@Override
						public String getKey(TableColumnModel item) {
							return item.getName();
						}
					});
		}

		return store;
	}
}
