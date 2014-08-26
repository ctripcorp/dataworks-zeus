/**
 * 
 */
package com.taobao.zeus.web.platform.client.module.tablemanager.component;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.taobao.zeus.web.platform.client.module.tablemanager.TableManagerPresenter;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

/**
 * @author gufei.wzy 2012-9-21
 */
public class TableInfoPanel extends TabPanel {

	private TableManagerPresenter presenter;
	private PlatformContext context;
	private ColumnInfoTab colInfoTab;
	private TableInfoTab tblInfoTab;
	private PartitionTab ptTab;
	private TableModel table;
	private boolean partitionLoaded = false;

	public TableInfoPanel(TableManagerPresenter c) {
		this.presenter = c;
		this.context = presenter.getPlatformContext();
		setBodyBorder(false);
		add(getTableInfoTab(), new TabItemConfig("基本信息"));
		add(getColumnInfoTab(), new TabItemConfig("字段信息"));
		add(getPtTab(), new TabItemConfig("分区信息"));

		addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				if (getConfig(event.getSelectedItem()).getText().equals("分区信息")) {
					if (isPartitionLoaded() == false) {
						getPtTab().load(table);
						setPartitionLoaded(true);
					}
				}
			}
		});
	}

	public void load(TableModel t) {
		this.table = t;
		getColumnInfoTab().load(t);
		getTableInfoTab().load(t);
		setPartitionLoaded(false);
		setActiveWidget(getWidget(0));
	}

	public TableModel getTable() {
		return table;
	}

	public void setTable(TableModel table) {
		this.table = table;
	}

	public PlatformContext getContext() {
		return context;
	}

	public TableInfoTab getTableInfoTab() {
		if (this.tblInfoTab == null) {
			this.tblInfoTab = new TableInfoTab(this);
		}
		return tblInfoTab;
	}

	private ColumnInfoTab getColumnInfoTab() {
		if (colInfoTab == null) {
			colInfoTab = new ColumnInfoTab(this);
		}
		return this.colInfoTab;
	}

	public PartitionTab getPtTab() {
		if (ptTab == null) {
			ptTab = new PartitionTab(this);
		}
		return ptTab;
	}

	protected boolean isPartitionLoaded() {
		return partitionLoaded;
	}

	protected void setPartitionLoaded(boolean partitionLoaded) {
		this.partitionLoaded = partitionLoaded;
	}

	public TableManagerPresenter getPresenter() {
		return presenter;
	}
}