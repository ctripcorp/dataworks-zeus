package com.taobao.zeus.web.platform.client.module.word.component;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCloseEvent.BeforeCloseHandler;
import com.sencha.gxt.widget.core.client.event.CloseEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugService;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugServiceAsync;

public class LogTabPanel extends TabPanel {
	
	protected static JobDebugServiceAsync debugService=GWT.create(JobDebugService.class);
	
	public LogTabPanel(){
		final MessageBox box = new MessageBox(
				"取消调试么?", "此次调试正在运行，要取消本次调试么？");
		box.setPredefinedButtons(
				PredefinedButton.YES,
				PredefinedButton.NO,
				PredefinedButton.CANCEL);
		box.setIcon(MessageBox.ICONS
				.question());
		box.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				final LogTextPanel item = (LogTextPanel) box.getData("item");
				Dialog btn = (Dialog) event.getSource();
				String choice = btn.getHideButton().getItemId();
				if(choice.equalsIgnoreCase("yes")){
					debugService.cancelDebug(item.getDebugId(), new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							item.getTimer().cancel();
							closeTab(item);
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("调试取消失败");
						}
					});
				}else if(choice.equalsIgnoreCase("no")){
					item.getTimer().cancel();
					closeTab(item);
				}else{
					//do nothing
				}
			}
		});
		addBeforeCloseHandler(new BeforeCloseHandler<Widget>() {
			@Override
			public void onBeforeClose(
					final BeforeCloseEvent<Widget> bcEvent) {
				final LogTextPanel item = (LogTextPanel) bcEvent.getItem();
				box.setData("item", item);
				if(item.isRunning() == true){
					box.show();
					bcEvent.setCancelled(true);
				}
			}
		});
	}
	public void closeTab(Widget w){
		remove(w);
		fireEvent(new CloseEvent<Widget>(w));
	}
	public void onStartDebug(final String debugId){
		TabItemConfig config = new TabItemConfig("ID:"+debugId, true);;
		final LogTextPanel logTextPanel = new LogTextPanel(debugId, config, this);
		add(logTextPanel, config);
		this.setActiveWidget(logTextPanel);
		logTextPanel.getTimer().run();
	}
}
