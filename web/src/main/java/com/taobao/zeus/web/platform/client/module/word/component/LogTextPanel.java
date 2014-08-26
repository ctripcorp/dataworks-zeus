package com.taobao.zeus.web.platform.client.module.word.component;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.taobao.zeus.web.platform.client.module.word.images.Images;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryModel;
import com.taobao.zeus.web.platform.client.util.ToolUtil;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugService;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugServiceAsync;

public class LogTextPanel extends ContentPanel{
	private boolean isRunning = true;
	private String debugId;
	protected static JobDebugServiceAsync debugService=GWT.create(JobDebugService.class);
	
	private Menu contextMenu = new Menu();
	private final MenuItem stop;
	private Timer timer;
	private final ScrollPanel logTextScrollPanel= new ScrollPanel();
	private final HTML html=new HTML();
	
	public LogTextPanel(final String debugId, final TabItemConfig config, final LogTabPanel logTabPanel){
		
		this.debugId = debugId;

		setHeaderVisible(false);
		
		setCollapsible(false);
		
		setContextMenu(contextMenu);
				
		stop = new MenuItem("停止运行", new SelectionHandler<MenuItem>() {
			
			@Override
			public void onSelection(final SelectionEvent<MenuItem> se) {
				final MessageBox box = new MessageBox("停止调试？", "确定要停止调试么？");
				box.setPredefinedButtons(
						PredefinedButton.YES,
						PredefinedButton.NO);
				box.setIcon(MessageBox.ICONS
						.question());
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						Dialog btn = (Dialog) event.getSource();
						String choice = btn.getHideButton().getItemId();
						if(choice.equalsIgnoreCase("yes")){
							debugService.cancelDebug(debugId, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void result) {
									se.getSelectedItem().setEnabled(false);
								}
								
								@Override
								public void onFailure(Throwable caught) {
											Window.alert("取消调试失败！\n原因："
													+ caught);
								}
							});
						}
					}
				});
				box.show();
			}
		});
		
		contextMenu.add(stop);

		html.addStyleName("console-font");
		logTextScrollPanel.setWidget(html);

		this.add(logTextScrollPanel);
		
		timer = new Timer() {
			public void run() {
				final LogTextPanel logTextPanel = LogTextPanel.this;
				debugService.getHistoryModel(debugId, new AsyncCallback<DebugHistoryModel>() {
					
					@Override
					public void onSuccess(DebugHistoryModel result) {
						// 刷新日志
						String[] lines=result.getLog().split("\n");
						StringBuffer sb=new StringBuffer();
						for(String line:lines){
							for(long i=1;i<9;i++){
								line = line.replace((char)('\u0000'+i), (char)('\u245f'+i));
							}
							String job=ToolUtil.extractJobId(line);
							if(job!=null){
								String newline = line.replaceFirst(job,
										"<a target='_blank' href='jobdetail.jsp?type=3&jobid="
												+ job + ">"
												+ job + "</a>");
								sb.append(newline+"<br/>");
							}else{
								sb.append(line+"<br/>");
							}
						}
						//不在底部的时候不自动滚动
						boolean needScroll = (logTextScrollPanel.getMaximumVerticalScrollPosition()
								- logTextScrollPanel.getVerticalScrollPosition() <14);
						//判断后才把新内容填进去
						html.setHTML(sb.toString());
						if (needScroll) {
							logTextScrollPanel.scrollToBottom();
						}
						
						// 更新状态
						if(result.getStatus().equals(DebugHistoryModel.Status.SUCCESS) ){
							config.setIcon(Images.getImageResources().traffic_green());
							stop.setEnabled(false);
							isRunning=false;
							cancel();
						}else if(result.getStatus().equals(DebugHistoryModel.Status.FAILED)){
							config.setIcon(Images.getImageResources().traffic_red());
							stop.setEnabled(false);
							isRunning=false;
							cancel();
						}else{
							config.setIcon(Images.getImageResources().processingIcon());
							stop.setEnabled(true);
							isRunning=true;
						}
						logTabPanel.update(logTextPanel, config);
					}

					@Override
					public void onFailure(Throwable caught) {
								// 忽略日志刷新失败
					}
				});
			}
		};
		timer.scheduleRepeating(2000);
	}

	@Override
	protected void onDetach() {
		timer.cancel();
		super.onDetach();
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public MenuItem getStop() {
		return stop;
	}

	public boolean isRunning() {
		return isRunning;
	}
	
	public String getDebugId(){
		return debugId;
	}
}