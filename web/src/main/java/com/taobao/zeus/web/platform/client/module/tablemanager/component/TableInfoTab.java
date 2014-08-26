package com.taobao.zeus.web.platform.client.module.tablemanager.component;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.widget.TitledCell;

/**
 * 基本信息tab
 * 
 * @author gufei.wzy 2012-9-21
 */
public class TableInfoTab implements IsWidget {

	@SuppressWarnings("unused")
	private TableInfoPanel panel;
	@SuppressWarnings("unused")
	private PlatformContext ctx;
	private final ModelPropertyModelProperties props = GWT
			.create(ModelPropertyModelProperties.class);
	private ListStore<ModelPropertyModel> store;

	public TableInfoTab(TableInfoPanel p) {
		this.panel = p;
		this.ctx = p.getContext();
	}

	@Override
	public Widget asWidget() {
		ColumnConfig<ModelPropertyModel, String> name = new ColumnConfig<TableInfoTab.ModelPropertyModel, String>(
				props.name(), 100, "Name");
		ColumnConfig<ModelPropertyModel, String> value = new ColumnConfig<TableInfoTab.ModelPropertyModel, String>(
				props.value(), 180, "Value");
		value.setCell(new TitledCell() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				if (value == null)
					value = "";
				if (value.startsWith("http://") || value.startsWith("https://")) {
					sb.appendHtmlConstant(
							"<a title=\"" + value + "\"" + "href=\"" + value
									+ "\" target=\"_blank\"" + ">")
							.appendHtmlConstant(value)
							.appendHtmlConstant("</a>");
				} else {
					super.render(context, value, sb);
				}
			}
		});
		List<ColumnConfig<ModelPropertyModel, ?>> colList = new ArrayList<ColumnConfig<ModelPropertyModel, ?>>(
				2);
		colList.add(name);
		colList.add(value);

		ColumnModel<ModelPropertyModel> colModel = new ColumnModel<TableInfoTab.ModelPropertyModel>(
				colList);
		Grid<ModelPropertyModel> grid = new Grid<TableInfoTab.ModelPropertyModel>(
				getStore(), colModel);
		grid.setAllowTextSelection(true);
		grid.getView().setForceFit(true);
		grid.getView().setAutoFill(true);
		final GridEditing<ModelPropertyModel> editing = new GridInlineEditing<ModelPropertyModel>(
				grid);
		editing.addEditor(value, new TextField());
		editing.getEditor(value).setReadOnly(true);

		return grid;
	}

	protected class ModelPropertyModel {
		private String name;
		private String value;

		public ModelPropertyModel(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	protected interface ModelPropertyModelProperties extends
			PropertyAccess<ModelPropertyModel> {
		@Path("name")
		ModelKeyProvider<ModelPropertyModel> key();

		ValueProvider<ModelPropertyModel, String> value();

		ValueProvider<ModelPropertyModel, String> name();
	}

	public void load(TableModel t) {
		getStore().clear();
		if (t != null) {
			store.add(new ModelPropertyModel("表名", t.getName()));
			store.add(new ModelPropertyModel("所有者", t.getOwner()));
			store.add(new ModelPropertyModel("创建时间", DateTimeFormat
					.getFormat("yyyy-MM-d HH:mm:ss").format(t.getCreateDate())
					.toString()));
			store.add(new ModelPropertyModel("路径", t.getPath()));
			String inputFormat = t.getInputFormat();
			if (inputFormat.equals("org.apache.hadoop.mapred.TextInputFormat")) {
				inputFormat = "TextFile";
			} else if (inputFormat
					.equals("org.apache.hadoop.mapred.SequenceFileInputFormat")) {
				inputFormat = "SequenceFile";
			} else if (inputFormat
					.equals("org.apache.hadoop.hive.ql.io.RCFileInputFormat")) {
				inputFormat = "RCFile";
			}
			store.add(new ModelPropertyModel("存储格式", inputFormat));
			store.add(new ModelPropertyModel("注释", t.getComment()));
			char fieldDelim = t.getFieldDelim()==null? '\001':t.getFieldDelim().toCharArray()[0];
			store.add(new ModelPropertyModel("列分割符", fieldDelim+"("+getUnicode(fieldDelim)+")"));
			store.add(new ModelPropertyModel("公共表数据字典","请在代码中配置"));
		}
	}

	public static String getUnicode(char c) {
		String s = Integer.toHexString(c);
		if (s.length() == 1) {
			s = "\\u000" + s;
		} else if (s.length() == 2) {
			s = "\\u00" + s;
		} else if (s.length() == 3) {
			s = "\\u0" + s;
		} else {
			s = "\\u" + s;
		}
		return s;
	}

	private ListStore<ModelPropertyModel> getStore() {
		if (this.store == null) {
			this.store = new ListStore<TableInfoTab.ModelPropertyModel>(
					new ModelKeyProvider<ModelPropertyModel>() {
						@Override
						public String getKey(ModelPropertyModel item) {
							return item.getName();
						}
					});
		}
		return this.store;
	}

}
