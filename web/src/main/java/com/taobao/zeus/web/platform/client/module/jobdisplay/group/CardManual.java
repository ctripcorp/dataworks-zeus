package com.taobao.zeus.web.platform.client.module.jobdisplay.group;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.info.Info;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobHistoryModel;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobHistoryProperties;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.CardHistory.LogWindow;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.TreeKeyProviderTool;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeSelectEvent;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.Refreshable;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class CardManual extends CenterTemplate implements Refreshable<GroupModel>{
	private static JobHistoryProperties prop=GWT.create(JobHistoryProperties.class);
	private Grid<JobHistoryModel> grid;
	private ListStore<JobHistoryModel> store;
	
	public CardManual(final GroupPresenter presenter){
		IdentityValueProvider<JobHistoryModel> identity = new IdentityValueProvider<JobHistoryModel>();
		RowNumberer<JobHistoryModel> numberer = new RowNumberer<JobHistoryModel>(identity);
		ColumnConfig<JobHistoryModel,String> jobId=new ColumnConfig<JobHistoryModel, String>(prop.jobId(), 30, "JobId");
		ColumnConfig<JobHistoryModel,String> name=new ColumnConfig<JobHistoryModel, String>(prop.name(),100,"任务名称");
		ColumnConfig<JobHistoryModel,String> owner=new ColumnConfig<JobHistoryModel,String>(prop.owner(),50,"所有人");
		owner.setCell(new AbstractCell<String>(){
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				if(value.contains("\\")){
					value=value.substring(value.indexOf("\\")+1);
				}
				sb.appendHtmlConstant("<span title='"+value+"'>"+value+"</span>");
			}
			
		});
		name.setCell(new AbstractCell<String>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='"+value+"'>"+value+"</span>");
			}
		});
		ColumnConfig<JobHistoryModel,Date> startTime=new ColumnConfig<JobHistoryModel, Date>(prop.startTime(),70,"开始时间");
		startTime.setCell(new AbstractCell<Date>() {
			private DateTimeFormat format=DateTimeFormat.getFormat("MM月dd日 HH:mm");
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Date value, SafeHtmlBuilder sb) {
				if(value!=null){
					sb.appendHtmlConstant(format.format(value));
				}
			}
		});
		ColumnConfig<JobHistoryModel,String> executeHost=new ColumnConfig<JobHistoryModel, String>(prop.executeHost(),70,"执行服务器");
		ColumnConfig<JobHistoryModel,String> triggerType=new ColumnConfig<JobHistoryModel, String>(prop.triggerType(),50,"触发类型");
		triggerType.setCell(new AbstractCell<String>() {
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant(value);
			}
		});
		ColumnConfig<JobHistoryModel,String> illustrate=new ColumnConfig<JobHistoryModel, String>(prop.illustrate(),80,"说明");
		illustrate.setCell(new AbstractCell<String>() {
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant( "<span title='"+value+"'>"+value+"</span>");
			}
		});
		ColumnConfig<JobHistoryModel,JobHistoryModel> operate=new ColumnConfig<JobHistoryModel, JobHistoryModel>(new ValueProvider<JobHistoryModel, JobHistoryModel>() {
			public String getPath() {
				return null;
			}
			public JobHistoryModel getValue(JobHistoryModel object) {
				return object;
			}
			public void setValue(JobHistoryModel object, JobHistoryModel value) {}
		},100,"操作");
		operate.setCell(new AbstractCell<JobHistoryModel>("click") {
			public void render(com.google.gwt.cell.client.Cell.Context context,
					JobHistoryModel value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<a href='javascript:void(0)'>查看日志</a>&nbsp;&nbsp;<a href='javascript:void(0)'>取消任务</a>");
			}
			@Override
			public void onBrowserEvent(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, final JobHistoryModel value, NativeEvent event,
					ValueUpdater<JobHistoryModel> valueUpdater) {
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
				if("click".equalsIgnoreCase(event.getType())){
					EventTarget eventTarget = event.getEventTarget();
					Element t=Element.as(eventTarget);
					if( t instanceof AnchorElement){
						AnchorElement ae=t.cast();
						if("查看日志".equals(ae.getInnerText())){
							LogWindow win=new LogWindow();
							win.refreshId(value.getJobId(),value.getId());
						}else if("取消任务".equals(ae.getInnerText())){
							ConfirmMessageBox box=new ConfirmMessageBox("取消任务", "你确认取消该任务吗?");
							box.addHideHandler(new HideHandler() {
								public void onHide(HideEvent event) {
									Dialog btn = (Dialog) event.getSource();
									if(btn.getHideButton().getText().equalsIgnoreCase("yes")){
										grid.mask("取消任务中");
										RPCS.getJobService().cancel(value.getId(),new AbstractAsyncCallback<Void>(){
											public void onSuccess(Void result) {
												refresh(presenter.getGroupModel());
												grid.unmask();
												Info.display("操作成功", "任务已经取消");
											}
											@Override
											public void onFailure(Throwable caught) {
												refresh(presenter.getGroupModel());
												super.onFailure(caught);
												grid.unmask();
											}
										});
									}
								}
							});
							box.show();
						}
					}
				}
			}
		});
		ColumnModel<JobHistoryModel> cm=new ColumnModel(Arrays.asList(numberer,jobId,name,owner,startTime,executeHost,triggerType,illustrate,operate));
		
		
		store=new ListStore<JobHistoryModel>(prop.key());

		grid=new Grid<JobHistoryModel>(store, cm);
		grid.setLoadMask(true);
		grid.getView().setForceFit(true);
		
		grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellClick(CellDoubleClickEvent event) {
				int row=event.getRowIndex();
				JobHistoryModel model=grid.getStore().get(row);
				if(model!=null){
					TreeNodeSelectEvent te=new TreeNodeSelectEvent(TreeKeyProviderTool.genJobProviderKey(model.getJobId()));
					presenter.getPlatformContext().getPlatformBus().fireEvent(te);
				}
			}
		});
		
		setCenter(grid);
		
		addButton(new TextButton("返回", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				presenter.display(presenter.getGroupModel());
			}
		}));
		addButton(new TextButton("刷新",new SelectHandler() {
			public void onSelect(SelectEvent event) {
				refresh(presenter.getGroupModel());
			}
		}));
	}
	@Override
	public void refresh(GroupModel groupModel) {
		grid.mask("加载中，请稍候");
		RPCS.getJobService().getManualRunning(groupModel.getId(), new AbstractAsyncCallback<List<JobHistoryModel>>() {
			public void onSuccess(List<JobHistoryModel> result) {
				store.clear();
				store.addAll(result);
				grid.unmask();
			}
		});
	}
}
