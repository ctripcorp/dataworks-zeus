package com.taobao.zeus.web.platform.client.module.tablemanager.component;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.taobao.zeus.web.platform.client.module.tablemanager.TablePreviewModel;
import com.taobao.zeus.web.platform.client.widget.TitledCell;

/**
 * @author gufei.wzy 2012-10-23
 */
public class DataPreviewGrid implements IsWidget {
	private Grid<Tuple<Integer, List<String>>> grid;
	private ListStore<Tuple<Integer, List<String>>> store;

	public DataPreviewGrid(TablePreviewModel result) {
		List<ColumnConfig<Tuple<Integer, List<String>>, ?>> list = new ArrayList<ColumnConfig<Tuple<Integer, List<String>>, ?>>();
		int count = 0;
		for (String col : result.getHeaders()) {
			ColumnConfig<Tuple<Integer, List<String>>, String> cc = new ColumnConfig<Tuple<Integer, List<String>>, String>(
					new MyValueProvider(count), 80, col);
			cc.setToolTip(new SafeHtmlBuilder().appendHtmlConstant(col)
					.toSafeHtml());
			cc.setCell(new TitledCell());
			list.add(cc);
			count++;
		}
		getListStore().addAll(result.getData());
		ColumnModel<Tuple<Integer, List<String>>> colModel = new ColumnModel<Tuple<Integer, List<String>>>(
				list);
		grid = new Grid<Tuple<Integer, List<String>>>(getListStore(), colModel);

		grid.setHeight(175);

		grid.setAllowTextSelection(true);

		grid.getView().setAdjustForHScroll(false);
		grid.getView().setEmptyText("没有数据！");
	}

	private class MyValueProvider implements
			ValueProvider<Tuple<Integer, List<String>>, String> {
		private int index;
		private String path;

		public MyValueProvider(int index) {
			this.index = index;
		}

		@Override
		public String getValue(Tuple<Integer, List<String>> object) {
			if (index >= object.getY().size())
				return "";
			return object.getY().get(index);
		}

		@Override
		public void setValue(Tuple<Integer, List<String>> object, String value) {
			object.getY().set(index, value);
		}

		@SuppressWarnings("unused")
		public void setPath(String path) {
			this.path = path;
		}

		@Override
		public String getPath() {
			return path;
		}
	}

	private ListStore<Tuple<Integer, List<String>>> getListStore() {
		if (store == null) {
			store = new ListStore<Tuple<Integer, List<String>>>(
					new ModelKeyProvider<Tuple<Integer, List<String>>>() {
						@Override
						public String getKey(Tuple<Integer, List<String>> data) {
							return data.getX().toString();
						}
					});
		}
		return store;
	}

	@Override
	public Widget asWidget() {
		return grid;
	}
}
