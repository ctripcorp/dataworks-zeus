package com.taobao.zeus.web.platform.client.module.jobdisplay.group;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.RefreshEvent.RefreshHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobHistoryModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
//import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModelAction;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModelPropertiesAction;
import com.taobao.zeus.web.platform.client.module.jobmanager.TreeKeyProviderTool;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeSelectEvent;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.Refreshable;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
/**
 * 任务总览
 * @author zhoufang
 *
 */
public class CardOverall extends CenterTemplate implements Refreshable<GroupModel>{

	private GroupPresenter presenter;

	@Override
	public void refresh(GroupModel t) {
		PagingLoadConfig config=new PagingLoadConfigBean();
		config.setOffset(0);
		config.setLimit(50);
		loader.load(config);
	}

	private Grid<JobModelAction> grid;
	private ListStore<JobModelAction> store;
	private PagingLoader<PagingLoadConfig,PagingLoadResult<JobModelAction>> loader;
	private PagingToolBar toolBar;
	private HorizontalLayoutContainer toolArea;
	
	private TextButton queryButton = null;
	private DateField start = null;
	private DateField end = null;
	
	private static JobModelPropertiesAction prop=GWT.create(JobModelPropertiesAction.class);
	
	public CardOverall(GroupPresenter p){
		//FIXME lky

		
		start = new DateField();
		start = new DateField();
		start.setEditable(false);
		start.setAllowBlank(false);
		start.setValue(new Date());
		
		end = new DateField();
		end = new DateField();
		end.setEditable(false);
		end.setAllowBlank(false);
		end.setValue(new Date());
		
		queryButton=new TextButton("查询",new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				if (!start.validate() || !end.validate()) {
					return;
				}else {
					PagingLoadConfig config=new PagingLoadConfigBean();
					config.setOffset(0);
					config.setLimit(50);
					loader.load(config);
				}
				
			}
		});


		toolArea=new HorizontalLayoutContainer();
		toolArea.add(new FieldLabel(start,"开始日期"),new HorizontalLayoutData(-1,-1,new Margins(5)));
		toolArea.add(new FieldLabel(end,"结束日期"),new HorizontalLayoutData(-1,-1,new Margins(5)));
		toolArea.add(queryButton,new HorizontalLayoutData(-1,-1,new Margins(5)));
		toolArea.addAttachHandler(new Handler(){

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				queryButton.fireEvent(new SelectEvent());
			}}			
		);
		
		
		this.presenter=p;
		ColumnConfig<JobModelAction,String> jobId=new ColumnConfig<JobModelAction,String>(prop.id(), 60,"ActionId");
		ColumnConfig<JobModelAction,String> toJobId=new ColumnConfig<JobModelAction,String>(prop.toJobId(), 20,"JobId");
		ColumnConfig<JobModelAction,String> jobName=new ColumnConfig<JobModelAction,String>(prop.name(),80, "任务名称");
		jobName.setCell(new AbstractCell<String>(){
			public void render(Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='"+value+"'>"+value+"</span>");
			}
		});
		ColumnConfig<JobModelAction,String> status=new ColumnConfig<JobModelAction,String>(prop.status(), 30,"执行状态");
		ColumnConfig<JobModelAction,String> lastStatus=new ColumnConfig<JobModelAction,String>(prop.lastStatus(),100, "上一次任务情况");
		lastStatus.setCell(new AbstractCell<String>() {
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='"+value+"'>"+value+"</span>");
			}
		});
		ColumnConfig<JobModelAction,Map<String, String>> readyDependency=new ColumnConfig<JobModelAction,Map<String, String>>(prop.readyDependencies(), 100,"依赖状态");
		readyDependency.setCell(new AbstractCell<Map<String, String>>() {
			private DateTimeFormat format=DateTimeFormat.getFormat("MM月dd日 HH:mm");
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Map<String, String> value, SafeHtmlBuilder sb) {
				if(value!=null && !value.isEmpty()){
					for(String key:value.keySet()){
						String time=value.get(key);
						if(time==null){
							sb.appendHtmlConstant("<span style='color:red'>依赖任务:"+key+",未执行</span>").appendHtmlConstant("<br/>");
						}else{
							sb.appendHtmlConstant("依赖任务:"+key+",运行时间").appendHtmlConstant(":")
							.appendHtmlConstant(format.format(new Date(Long.valueOf(time)))).appendHtmlConstant("<br/>");
						}
					}
				}else{
					sb.appendHtmlConstant("独立任务");
				}
			}
		});
		ColumnModel cm=new ColumnModel(Arrays.asList(jobId,toJobId,jobName,status,readyDependency,lastStatus));
		
		RpcProxy<PagingLoadConfig,PagingLoadResult<JobModelAction>> proxy=new RpcProxy<PagingLoadConfig,PagingLoadResult<JobModelAction>>(){
			@Override
			public void load(PagingLoadConfig loadConfig,
					AsyncCallback<PagingLoadResult<JobModelAction>> callback) {
				PagingLoadConfig config=(PagingLoadConfig)loadConfig;
				RPCS.getJobService().getSubJobStatus(presenter.getGroupModel().getId(),config, start.getValue(),end.getValue(), callback);				
			}
		};
		store=new ListStore<JobModelAction>(new ModelKeyProvider<JobModelAction>() {
			public String getKey(JobModelAction item) {
				return item.getId();
			}
		});
		loader=new PagingLoader<PagingLoadConfig,PagingLoadResult<JobModelAction>>(proxy);
		loader.setLimit(50);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig,JobModelAction,PagingLoadResult<JobModelAction>>(store));

		
		
		grid=new Grid<JobModelAction>(store, cm);
		grid.setLoadMask(true);
		grid.getView().setForceFit(true);
		
		grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			public void onCellClick(CellDoubleClickEvent event) {
				int row=event.getRowIndex();
				JobModelAction model=grid.getStore().get(row);
				if(model!=null){
					TreeNodeSelectEvent te=new TreeNodeSelectEvent(TreeKeyProviderTool.genJobProviderKey(model.getToJobId()));
					presenter.getPlatformContext().getPlatformBus().fireEvent(te);
				}
			}
		});
		grid.addRefreshHandler(new RefreshHandler() {
			public void onRefresh(RefreshEvent event) {
				for(JobModelAction m:store.getAll()){
					if(m.getAuto()){
						grid.getView().getRow(m).setAttribute("style", "color:green");
					}
				}
			}
		});
		
		toolBar = new PagingToolBar(50);
		toolBar.bind(loader);
		VerticalLayoutContainer con = new VerticalLayoutContainer();
	    con.setBorders(true);
	    con.add(toolArea,new VerticalLayoutData(1,30));
	    con.add(grid, new VerticalLayoutData(1, 1));
	    con.add(toolBar, new VerticalLayoutData(1, 30));

		setCenter(con);
		
		addButton(new TextButton("返回",new SelectHandler() {
			public void onSelect(SelectEvent event) {
				presenter.display(presenter.getGroupModel());
			}
		}));
		
		addButton(new TextButton("自动任务",new SelectHandler() {
			public void onSelect(SelectEvent event) {
				presenter.displayRunning();
			}
		}));
		
		addButton(new TextButton("手动任务",new SelectHandler() {
			public void onSelect(SelectEvent event) {
				presenter.displayManual();
			}
		}));

		
		

		
	}
	
}

