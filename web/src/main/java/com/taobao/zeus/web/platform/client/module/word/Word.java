package com.taobao.zeus.web.platform.client.module.word;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.theme.blue.client.tabs.BlueTabPanelBottomAppearance;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugService;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugServiceAsync;

public abstract class Word extends TabPanel implements IsWidget {

	protected WordPresenter presenter;
	protected PlatformContext context;
	protected FileModel model;
	protected Menu contextMenu;
	protected static JobDebugServiceAsync debugService = GWT
			.create(JobDebugService.class);


	public Word(PlatformContext context, WordPresenter presenter) {
		super(
				GWT.<TabPanelAppearance> create(BlueTabPanelBottomAppearance.class));
		this.presenter = presenter;
		this.context = context;
		getElement().setMargins(3);
	}

	protected SelectionHandler<MenuItem> themeHandler = new SelectionHandler<MenuItem>() {
		@Override
		public void onSelection(SelectionEvent<MenuItem> event) {
			CodeMirror.theme = event.getSelectedItem().getText();
			getCodeMirror().setOption("theme",
					event.getSelectedItem().getText());
		}
	};



	protected abstract CodeMirror getCodeMirror();

	public FileModel getFileModel() {
		return this.model;
	}
}
