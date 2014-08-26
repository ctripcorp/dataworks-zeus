package com.taobao.zeus.web.platform.client.module.tablemanager.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TableColumnModelProperties extends
		PropertyAccess<TableColumnModel> {

	@Path("name")
	ModelKeyProvider<TableColumnModel> key();

	ValueProvider<TableColumnModel, String> name();

	ValueProvider<TableColumnModel, String> type();

	ValueProvider<TableColumnModel, String> desc();

}
