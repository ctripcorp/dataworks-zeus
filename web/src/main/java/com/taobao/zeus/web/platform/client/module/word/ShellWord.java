package com.taobao.zeus.web.platform.client.module.word;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.CheckableJobTree;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupJobTreeModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.module.profile.QuickHadoopConfig;
import com.taobao.zeus.web.platform.client.module.word.DataxWidget.Callback;
import com.taobao.zeus.web.platform.client.module.word.component.EditTab;
import com.taobao.zeus.web.platform.client.module.word.component.HistoryTab;
import com.taobao.zeus.web.platform.client.module.word.component.ViewTab;
import com.taobao.zeus.web.platform.client.util.GWTEnvironment;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class ShellWord extends Word {

	public ShellWord(PlatformContext context,WordPresenter presenter,FileModel fmodel) {
		super(context ,presenter);
		this.model=fmodel;
		
		if(fmodel.isAdmin() || context.getUser().isSuper()){
			add(getEditTab(), new TabItemConfig("编辑", false));
			add(getHistoryTab(),new TabItemConfig("调试历史", false));
			setActiveWidget(editTab);
		}else{
			add(getViewTab(), new TabItemConfig("查看", false));
		}
		addBeforeSelectionHandler(new BeforeSelectionHandler<Widget>() {
					public void onBeforeSelection(
							BeforeSelectionEvent<Widget> event) {
						if (event.getItem() == getViewTab()) {
							if(editTab.getNewContent()!=null){
								getViewTab().refresh(editTab.getNewContent());
							}else if (model.getContent() != null) {
								getViewTab().refresh(model.getContent());
							}
						}else if(event.getItem()==getEditTab()){
							getEditTab().getCodeMirror().refresh();
						}else if(event.getItem() == getHistoryTab()){
							getHistoryTab().refresh();
						}
					}
				});
	}

	private ViewTab viewTab;
	private EditTab editTab;
	private HistoryTab historyTab;
	
	protected ViewTab getViewTab() {
		if (viewTab == null) {
			viewTab = new ViewTab(model);
		}
		return viewTab;
	}

	public EditTab getEditTab() {
		if (editTab == null) {
			editTab = new EditTab(context,presenter,model);
		}
		return editTab;
	}
	

	protected HistoryTab getHistoryTab() {
		if (historyTab == null) {
			historyTab = new HistoryTab(context,presenter,model);
		}
		return historyTab;
	}
	@Override
	protected CodeMirror getCodeMirror() {
		return getEditTab().getCodeMirror();
	}

}
