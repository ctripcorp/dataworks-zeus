package com.taobao.zeus.web.platform.client.module.word.component;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.theme.blue.client.status.BlueBoxStatusAppearance;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.StatusAppearance;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CloseEvent;
import com.sencha.gxt.widget.core.client.event.CloseEvent.CloseHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.OnChangeListener;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.word.WordPresenter;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.async.PlatformAsyncCallback;

public class EditTab extends BorderLayoutContainer {

	private FileModel model;

	@SuppressWarnings("unused")
	private WordPresenter presenter;

	private CodeMirror codeMirror;

	private Status status;
	private Status charCount;
	private Status workerGroupStatus;
	private LogTabPanel logTabPanel = new LogTabPanel();
	private VerticalLayoutContainer codePanel = new VerticalLayoutContainer();
	private ContentPanel logPanel = new ContentPanel();

	private String newContent;

	private PlatformContext context;
	
	public EditTab(PlatformContext context, WordPresenter presenter,
			final FileModel model) {
		this.context = context;
		this.presenter = presenter;
		this.model = model;

		ToolBar bar = new ToolBar();
		bar.add(new FillToolItem());

		charCount = new Status(
				GWT.<StatusAppearance> create(BlueBoxStatusAppearance.class));
		charCount.setWidth(150);
		if (model.getContent() == null)
			charCount.setText(0 + " 个字符");
		else
			charCount.setText(model.getContent().length() + " 个字符");
		bar.add(charCount);

		bar.add(new LabelToolItem(" "));

		status = new Status(
				GWT.<StatusAppearance> create(BlueBoxStatusAppearance.class));
		status.setText("已保存");
		status.setWidth(100);
		bar.add(status);
		bar.add(new LabelToolItem(" "));
		workerGroupStatus = new Status(
				GWT.<StatusAppearance> create(BlueBoxStatusAppearance.class));
		workerGroupStatus.setText("运行worker组id： " + model.getWorkerGroupId());
		workerGroupStatus.setWidth(150);
		bar.add(workerGroupStatus);

		codePanel.add(getCodeMirror(), new VerticalLayoutData(1, 1,
				new Margins(5)));
		bar.setLayoutData(new VerticalLayoutData(1, -1));
		
		codePanel.add(bar);
		setCenterWidget(codePanel, new MarginData(0, 0, 5, 0));

		if (model.getName().endsWith(".sh")
				|| model.getName().endsWith(".hive") || model.getName().endsWith(".odps")) {
			logTabPanel.addCloseHandler(new CloseHandler<Widget>() {
				public void onClose(CloseEvent<Widget> event) {
					if (logTabPanel.getWidgetCount() == 0) {
						EditTab.this.hide(LayoutRegion.SOUTH);
					}
				}
			});
			logPanel.setWidget(logTabPanel);
			logPanel.setHeaderVisible(false);
			logTabPanel.setTabScroll(true);
			logTabPanel.setCloseContextMenu(true);
			logPanel.addAttachHandler(new com.google.gwt.event.logical.shared.AttachEvent.Handler() {
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					if (event.isAttached())
						EditTab.this.hide(LayoutRegion.SOUTH);
				}
			});
			BorderLayoutData data = new BorderLayoutData();
			data.setSize(200);
//			data.setCollapsed(true);
//			data.setCollapseMini(true);
			data.setCollapsible(true);
			data.setSplit(true);
			setSouthWidget(logPanel, data);
		}

		getCodeMirror().addChangeListener(new OnChangeListener() {
			public void onChange() {
				newContent = getCodeMirror().getValue();
				status.setText("编辑中...");
				String value = getCodeMirror().getValue();
				int length = value != null ? value.length() : 0;
				charCount.setText(length + " 个字符");
				task.delay(1000);
			}
		});

	}

	public CodeMirror getCodeMirror() {
		if (codeMirror == null) {
			CodeMirrorConfig editCMC = new CodeMirrorConfig();
			editCMC.value = model.getContent();
			newContent = model.getContent();
			if (model.getName().endsWith(".hive")) {
				editCMC.mode = "mysql";
			} else if (model.getName().endsWith(".sh")) {
				editCMC.mode = "shell";
			} else if (model.getName().endsWith(".html")
					|| model.getName().endsWith(".htm")) {
				editCMC.mode = "htmlmixed";
			} else {
				editCMC.mode = "shell";
			}
			editCMC.readOnly = false;
			codeMirror = new CodeMirror(editCMC);
		}
		return codeMirror;
	}

	private DelayedTask task = new DelayedTask() {
		@Override
		public void onExecute() {
			if (newContent == null) {
				newContent = model.getContent();
				if (newContent == null) {
					newContent = "";
				}
			}
			if (!newContent.equals(model.getContent())) {
				getContext().getFileSystem().updateFileContent(model.getId(),
						newContent, new PlatformAsyncCallback<Void>() {
							public void callback(Void t) {
							}
						});
			}
			status.clearStatus("已保存");
		}
	};

	public String getNewContent() {
		return newContent;
	}

	public LogTabPanel getLogTabPanel() {
		return this.logTabPanel;
	}

	public ContentPanel getLogPanel() {
		return this.logPanel;
	}

	public VerticalLayoutContainer getCodePanel() {
		return codePanel;
	}

	public void refreshWorkerGroupStatus(String id) {
		workerGroupStatus.setText("运行worker组id： " + id);
	}

	public PlatformContext getContext() {
		return context;
	}
}
