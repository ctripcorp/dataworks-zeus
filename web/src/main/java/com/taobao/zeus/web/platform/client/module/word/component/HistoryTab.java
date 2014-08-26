package com.taobao.zeus.web.platform.client.module.word.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.word.WordPresenter;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryModel;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryModel.Status;
import com.taobao.zeus.web.platform.client.module.word.model.DebugHistoryProperties;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.ToolUtil;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.widget.MyRowExpander;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugService;
import com.taobao.zeus.web.platform.shared.rpc.JobDebugServiceAsync;

public class HistoryTab extends ContentPanel {

	protected static JobDebugServiceAsync debugService = GWT
			.create(JobDebugService.class);
	private final DebugHistoryProperties props = GWT
			.create(DebugHistoryProperties.class);
	private final ListStore<DebugHistoryModel> store = new ListStore<DebugHistoryModel>(
			new ModelKeyProvider<DebugHistoryModel>() {
				@Override
				public String getKey(DebugHistoryModel item) {
					return "" + item.getId();
				}
			});

	final PagingLoader<PagingLoadConfig, PagingLoadResult<DebugHistoryModel>> loader;

	public HistoryTab(PlatformContext context, WordPresenter presenter,
			final FileModel model) {
		RpcProxy<PagingLoadConfig, PagingLoadResult<DebugHistoryModel>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<DebugHistoryModel>>() {
			@Override
			public void load(PagingLoadConfig loadConfig,
					AsyncCallback<PagingLoadResult<DebugHistoryModel>> callback) {
				debugService.getDebugHistory(loadConfig, model.getId(),
						callback);
			}
		};

		loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<DebugHistoryModel>>(
				proxy);
		loader.setRemoteSort(true);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, DebugHistoryModel, PagingLoadResult<DebugHistoryModel>>(
				store));

		final PagingToolBar toolBar = new PagingToolBar(20);
		toolBar.bind(loader);

		List<ColumnConfig<DebugHistoryModel, ?>> l = new ArrayList<ColumnConfig<DebugHistoryModel, ?>>();

		ColumnModel<DebugHistoryModel> cm = new ColumnModel<DebugHistoryModel>(
				l);

		final Grid<DebugHistoryModel> grid = new Grid<DebugHistoryModel>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						loader.load();
					}
				});
			}
		};

		IdentityValueProvider<DebugHistoryModel> identity = new IdentityValueProvider<DebugHistoryModel>();

		final MyRowExpander<DebugHistoryModel> expander = new MyRowExpander<DebugHistoryModel>(
				identity, grid, new AbstractCell<DebugHistoryModel>() {

					@Override
					public void render(Context context,
							DebugHistoryModel value, SafeHtmlBuilder sb) {
						String[] lines=value.getLog().split("\n");
						StringBuffer logSb=new StringBuffer();
						for(String line:lines){
							String job=ToolUtil.extractJobId(line);
							if(job!=null){
								String newline = line.replaceFirst(job,
										"<a target='_blank' href='jobdetail.jsp?type=3&jobid="
												+ job + "'>" + job + "</a>");
								logSb.append(newline+"<br/>");
							}else{
								logSb.append(line+"<br/>");
							}
						}
						sb.appendHtmlConstant("<h2>日志<br/></h2>")
								.appendHtmlConstant(
										"<p class=\"console-font\"><br/>"
												+ logSb.toString() + "<br/></p>")
								.appendHtmlConstant("<h2>脚本</b><br/></h2>")
								.appendHtmlConstant(
										"<p class=\"console-font\"><br/>"
												+ value.getScript().replace(
														"\n", "<br/>")
												+ "<br/></p>");
					}
				});

		ColumnConfig<DebugHistoryModel, String> idColumn = new ColumnConfig<DebugHistoryModel, String>(
				props.id(), 60, "ID");
		ColumnConfig<DebugHistoryModel, String> executeHostColumn = new ColumnConfig<DebugHistoryModel, String>(
				props.executeHost(), 110, "执行机器IP");
		ColumnConfig<DebugHistoryModel, Status> statusColumn = new ColumnConfig<DebugHistoryModel, Status>(
				props.status(), 60, "状态");
		ColumnConfig<DebugHistoryModel, Date> startTimeColumn = new ColumnConfig<DebugHistoryModel, Date>(
				props.startTime(), 110, "开始时间");
		ColumnConfig<DebugHistoryModel, Date> endTimeColumn = new ColumnConfig<DebugHistoryModel, Date>(
				props.endTime(), 110, "结束时间");
		startTimeColumn.setCell(new DateCell(DateTimeFormat
				.getFormat(PredefinedFormat.DATE_TIME_SHORT)));
		endTimeColumn.setCell(new DateCell(DateTimeFormat
				.getFormat(PredefinedFormat.DATE_TIME_SHORT)));
		
		ColumnConfig<DebugHistoryModel,DebugHistoryModel> operate = new ColumnConfig<DebugHistoryModel, DebugHistoryModel>(
				new ValueProvider<DebugHistoryModel, DebugHistoryModel>() {
					public String getPath() {
						return null;
					}

					public DebugHistoryModel getValue(DebugHistoryModel object) {
						return object;
					}

					public void setValue(DebugHistoryModel object,
							DebugHistoryModel value) {
					}
				}, 120, "操作");
		operate.setCell(new AbstractCell<DebugHistoryModel>("click") {
			public void render(
					final com.google.gwt.cell.client.Cell.Context context,
					DebugHistoryModel value, SafeHtmlBuilder sb) {
				if (DebugHistoryModel.Status.RUNNING.equals(value.getStatus())) {
					sb.appendHtmlConstant("<a href='javascript:void(0)'>查看日志</a>&nbsp;&nbsp;<a href='javascript:void(0)'>取消调试</a>");
				} else {
					sb.appendHtmlConstant("<a href='javascript:void(0)'>查看日志</a>");
				}
			}

			@Override
			public void onBrowserEvent(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, final DebugHistoryModel value,
					NativeEvent event,
					ValueUpdater<DebugHistoryModel> valueUpdater) {
				super.onBrowserEvent(context, parent, value, event,
						valueUpdater);
				if ("click".equalsIgnoreCase(event.getType())) {
					EventTarget eventTarget = event.getEventTarget();
					Element t = Element.as(eventTarget);
					if (t instanceof AnchorElement) {
						AnchorElement ae = t.cast();
						if ("取消调试".equals(ae.getInnerText())) {
							ConfirmMessageBox box = new ConfirmMessageBox(
									"取消调试", "你确认取消此次调试吗?");
							box.addHideHandler(new HideHandler() {
								public void onHide(HideEvent event) {
									Dialog btn = (Dialog) event.getSource();
									if (btn.getHideButton().getText()
											.equalsIgnoreCase("yes")) {
										grid.mask("取消调试中");
										RPCS.getJobDebugService()
												.cancelDebug(
														value.getId(),
														new AbstractAsyncCallback<Void>() {
															public void onSuccess(
																	Void result) {
																loader.load();
																grid.unmask();
																Info.display(
																		"操作成功",
																		"本次调试已经取消");
															}

															@Override
															public void onFailure(
																	Throwable caught) {
																super.onFailure(caught);
																loader.load();
																grid.unmask();
															}
														});
									}
								}
							});
							box.show();
						} else if ("查看日志".equals(ae.getInnerText())) {
							expander.toggleExpand(context.getIndex());
						}
					}
				}
			}
		});

		l.add(expander);
		l.add(idColumn);
		l.add(executeHostColumn);
		l.add(statusColumn);
		l.add(startTimeColumn);
		l.add(endTimeColumn);
		l.add(operate);
		grid.setAllowTextSelection(true);
		grid.setLoadMask(true);
		grid.setLoader(loader);
		grid.getView().setAutoExpandColumn(expander);
		grid.getView().setForceFit(true);
		grid.getView().setSortingEnabled(false);
		expander.initPlugin(grid);
		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(grid, new VerticalLayoutData(1, 1));
		con.add(toolBar, new VerticalLayoutData(1, 30));

		add(con,new MarginData(3));
		setHeaderVisible(false);

		grid.addRowDoubleClickHandler(new RowDoubleClickHandler() {

			@Override
			public void onRowDoubleClick(RowDoubleClickEvent event) {
				expander.toggleExpand(event.getRowIndex());
			}
		});

	}

	public void refresh() {
		loader.load();
	}

}