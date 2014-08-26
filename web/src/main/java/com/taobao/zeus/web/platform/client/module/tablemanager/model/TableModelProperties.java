package com.taobao.zeus.web.platform.client.module.tablemanager.model;

import java.util.Date;
import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TableModelProperties extends PropertyAccess<TableModel> {

	@Path("name")
	ModelKeyProvider<TableModel> key();

	ValueProvider<TableModel, String> dbName();

	ValueProvider<TableModel, Date> createDate();

	ValueProvider<TableModel, String> name();

	ValueProvider<TableModel, String> path();

	ValueProvider<TableModel, String> comment();

	ValueProvider<TableModel, String> owner();

	ValueProvider<TableModel, List<TableColumnModel>> cols();

	ValueProvider<TableModel, List<String>> partitions();

	ValueProvider<TableModel, String> fieldDelim();

	ValueProvider<TableModel, String> lineDelim();

	ValueProvider<TableModel, String> serDeClass();

}
