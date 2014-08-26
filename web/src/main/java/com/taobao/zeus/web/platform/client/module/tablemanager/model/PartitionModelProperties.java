package com.taobao.zeus.web.platform.client.module.tablemanager.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface PartitionModelProperties extends
		PropertyAccess<PartitionModel> {

	@Path("name")
	ModelKeyProvider<PartitionModel> key();

	ValueProvider<PartitionModel, String> name();

	ValueProvider<PartitionModel, String> path();

	ValueProvider<PartitionModel, String> size();

}
