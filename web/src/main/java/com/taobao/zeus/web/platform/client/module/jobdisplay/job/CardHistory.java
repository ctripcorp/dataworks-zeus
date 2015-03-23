package com.taobao.zeus.web.platform.client.module.jobdisplay.job;

import java.util.Arrays;
import java.util.Date;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.Refreshable;
import com.taobao.zeus.web.platform.client.util.ToolUtil;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class CardHistory extends CenterTemplate implements
		Refreshable<JobModel> {

	private static JobHistoryProperties prop = GWT
			.create(JobHistoryProperties.class);
	private JobPresenter presenter;
	private Grid<JobHistoryModel> grid;
	private ListStore<JobHistoryModel> store;
	private PagingLoader<PagingLoadConfig, PagingLoadResult<JobHistoryModel>> loader;
	private PagingToolBar toolBar;

	public static class LogWindow extends Window {

		private String id;
		private BorderLayoutContainer container=new BorderLayoutContainer();
		private ScrollPanel html;
		private Frame frame=new Frame();
		private Timer timer;
		private String lastContent;

		public LogWindow() {
			html = new ScrollPanel();
			html.addStyleName("console-font");
			add(container);
			container.setCenterWidget(html);
			frame.setHeight("140px");
			frame.getElement().setAttribute("style", "border-width:0px");
			BorderLayoutData layoutData=new BorderLayoutData(140);
			container.setSouthWidget(frame, layoutData);
			setSize("700", "500");
			setHeadingText("日志");
			setMaximizable(true);
			timer = new Timer() {
				public void run() {
					RPCS.getJobService().getJobHistory(id,
							new AbstractAsyncCallback<JobHistoryModel>() {
								@Override
								public void onSuccess(JobHistoryModel result) {
									String lastJobId=null;
									if (!result.getLog().equals(lastContent)) {
										String[] lines=result.getLog().split("\n");
										StringBuffer sb=new StringBuffer();
										for(String line:lines){
											String job=ToolUtil.extractJobId(line);
											if(job!=null){
												lastJobId=job;
												String newline=line.replaceAll(job, "<a target='_blank' href='jobdetail.jsp?type=2&jobid="+job+">"+job+"</a>");
												sb.append(newline+"<br/>");
											}else{
												sb.append(line+"<br/>");
											}
										}
										boolean needScroll=(html.getMaximumVerticalScrollPosition()==html.getVerticalScrollPosition());
										html.getElement().setInnerHTML(sb.toString());
										if(needScroll){
											html.scrollToBottom();
										}
										if(result.getEndTime()!=null || lastJobId==null){
											container.hide(LayoutRegion.SOUTH);
										}else if(lastJobId!=null){
											String url="jobdetail.jsp?type=1&jobId="+lastJobId+"&timestamp="+new Date().getTime();
											frame.setUrl(url);
											container.show(LayoutRegion.SOUTH);
										}
										lastContent = result.getLog();
									}
									if (result.getEndTime() != null) {
										timer.cancel();
										return;
									}
								}
							});
				}
			};
			addHideHandler(new HideHandler() {
				public void onHide(HideEvent event) {
					timer.cancel();
				}
			});
		}

		public void refreshId(String jobId,String hisId) {
			this.id = hisId;
			setHeadingText("任务ID："+jobId+" 记录ID:" + id);
			timer.run();
			timer.scheduleRepeating(2000);
			if (!isVisible()) {
				show();
			}
		}

	}

	public CardHistory(JobPresenter p) {
		this.presenter = p;

		ColumnConfig<JobHistoryModel, String> id = new ColumnConfig<JobHistoryModel, String>(
				prop.id(), 30, "id");
		ColumnConfig<JobHistoryModel, String> jobId = new ColumnConfig<JobHistoryModel, String>(
				prop.jobId(), 80, "ActionId");
		ColumnConfig<JobHistoryModel, String> toJobId = new ColumnConfig<JobHistoryModel, String>(
				prop.toJobId(), 40, "JobId");
		ColumnConfig<JobHistoryModel, Date> startTime = new ColumnConfig<JobHistoryModel, Date>(
				prop.startTime(), 80, "开始时间");
		startTime.setCell(new AbstractCell<Date>() {
			private DateTimeFormat format = DateTimeFormat
					.getFormat("MM月dd日 HH:mm:ss");

			public void render(com.google.gwt.cell.client.Cell.Context context,
					Date value, SafeHtmlBuilder sb) {
				if (value != null) {
					sb.appendHtmlConstant(format.format(value));
				}
			}
		});
		ColumnConfig<JobHistoryModel, Date> endTime = new ColumnConfig<JobHistoryModel, Date>(
				prop.endTime(), 80, "结束时间");
		endTime.setCell(new AbstractCell<Date>() {
			private DateTimeFormat format = DateTimeFormat
					.getFormat("MM月dd日 HH:mm:ss");

			public void render(com.google.gwt.cell.client.Cell.Context context,
					Date value, SafeHtmlBuilder sb) {
				if (value != null) {
					sb.appendHtmlConstant(format.format(value));
				}
			}
		});
		ColumnConfig<JobHistoryModel, String> executeHost = new ColumnConfig<JobHistoryModel, String>(
				prop.executeHost(), 80, "执行服务器");
		ColumnConfig<JobHistoryModel, String> status = new ColumnConfig<JobHistoryModel, String>(
				prop.status(), 50, "执行状态");
		ColumnConfig<JobHistoryModel, String> triggerType = new ColumnConfig<JobHistoryModel, String>(
				prop.triggerType(), 50, "触发类型");
		ColumnConfig<JobHistoryModel, String> operator = new ColumnConfig<JobHistoryModel, String>(
				prop.operator(), 80, "执行人");
		operator.setCell(new AbstractCell<String>() {
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='" + value + "'>" + value
						+ "</span>");
			}
		});
		ColumnConfig<JobHistoryModel, String> illustrate = new ColumnConfig<JobHistoryModel, String>(
				prop.illustrate(), 100, "说明");
		illustrate.setCell(new AbstractCell<String>() {
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='" + value + "'>" + value
						+ "</span>");
			}
		});
		ColumnConfig<JobHistoryModel, String> statisEndTime = new ColumnConfig<JobHistoryModel, String>(
				prop.statisEndTime(), 80, "统计时间");
		ColumnConfig<JobHistoryModel, String> timezone = new ColumnConfig<JobHistoryModel, String>(
				prop.timeZone(), 50, "时区");
		ColumnConfig<JobHistoryModel, String> cycle = new ColumnConfig<JobHistoryModel, String>(
				prop.cycle(), 50, "任务周期");
		ColumnConfig<JobHistoryModel, JobHistoryModel> operate = new ColumnConfig<JobHistoryModel, JobHistoryModel>(
				new ValueProvider<JobHistoryModel, JobHistoryModel>() {
					public String getPath() {
						return null;
					}

					public JobHistoryModel getValue(JobHistoryModel object) {
						return object;
					}

					public void setValue(JobHistoryModel object,
							JobHistoryModel value) {
					}
				}, 80, "操作");
		operate.setCell(new AbstractCell<JobHistoryModel>("click") {
			public void render(
					final com.google.gwt.cell.client.Cell.Context context,
					JobHistoryModel value, SafeHtmlBuilder sb) {
				if ("running".equalsIgnoreCase(value.getStatus())) {
					sb.appendHtmlConstant("<a href='javascript:void(0)'>查看日志</a>&nbsp;&nbsp;<a href='javascript:void(0)'>取消任务</a>");
				} else {
					sb.appendHtmlConstant("<a href='javascript:void(0)'>查看日志</a>");
				}
			}

			@Override
			public void onBrowserEvent(
					com.google.gwt.cell.client.Cell.Context context,
					Element parent, final JobHistoryModel value,
					NativeEvent event,
					ValueUpdater<JobHistoryModel> valueUpdater) {
				super.onBrowserEvent(context, parent, value, event,
						valueUpdater);
				if ("click".equalsIgnoreCase(event.getType())) {
					EventTarget eventTarget = event.getEventTarget();
					Element t = Element.as(eventTarget);
					if (t instanceof AnchorElement) {
						AnchorElement ae = t.cast();
						if ("查看日志".equals(ae.getInnerText())) {
							LogWindow win = new LogWindow();
							win.refreshId(value.getJobId(),value.getId());
						} else if ("取消任务".equals(ae.getInnerText())) {
							ConfirmMessageBox box = new ConfirmMessageBox(
									"取消任务", "你确认取消该任务吗?");
							box.addHideHandler(new HideHandler() {
								public void onHide(HideEvent event) {
									Dialog btn = (Dialog) event.getSource();
									if (btn.getHideButton().getText()
											.equalsIgnoreCase("yes")) {
										grid.mask("取消任务中");
										RPCS.getJobService()
												.cancel(value.getId(),
														new AbstractAsyncCallback<Void>() {
															public void onSuccess(
																	Void result) {
																refresh(presenter
																		.getJobModel());
																grid.unmask();
																Info.display(
																		"操作成功",
																		"任务已经取消");
															}

															@Override
															public void onFailure(
																	Throwable caught) {
																super.onFailure(caught);
																refresh(presenter
																		.getJobModel());
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
		ColumnModel<JobHistoryModel> cm = new ColumnModel(Arrays.asList(id,
				jobId, toJobId, startTime, endTime, executeHost, status, operator,
				triggerType, illustrate,statisEndTime,timezone,cycle,operate));

		RpcProxy<PagingLoadConfig, PagingLoadResult<JobHistoryModel>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<JobHistoryModel>>() {
			@Override
			public void load(PagingLoadConfig loadConfig,
					AsyncCallback<PagingLoadResult<JobHistoryModel>> callback) {
				RPCS.getJobService().jobHistoryPaging(
						presenter.getJobModel().getId(), loadConfig, callback);
			}
		};

		store = new ListStore<JobHistoryModel>(
				new ModelKeyProvider<JobHistoryModel>() {
					public String getKey(JobHistoryModel item) {
						return item.getId();
					}
				});

		loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<JobHistoryModel>>(
				proxy);
		loader.setLimit(50);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, JobHistoryModel, PagingLoadResult<JobHistoryModel>>(
				store));

		grid = new Grid<JobHistoryModel>(store, cm);
		grid.setLoadMask(true);
		grid.getView().setForceFit(true);
		grid.setLoader(loader);

		toolBar = new PagingToolBar(50);
		toolBar.bind(loader);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);
		con.add(grid, new VerticalLayoutData(1, 1));
		con.add(toolBar, new VerticalLayoutData(1, 30));

		setCenter(con);

		addButton(new TextButton("返回", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				presenter.display(presenter.getJobModel());
			}
		}));

		addButton(new TextButton("刷新", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				refresh(presenter.getJobModel());
			}
		}));
	}

	@Override
	public void refresh(JobModel t) {
		PagingLoadConfig config = new PagingLoadConfigBean();
		config.setOffset(0);
		config.setLimit(50);
		loader.load(config);
	}

}
