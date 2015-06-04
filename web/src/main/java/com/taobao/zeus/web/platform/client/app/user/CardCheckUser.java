package com.taobao.zeus.web.platform.client.app.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.Refreshable;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class CardCheckUser extends CenterTemplate implements Refreshable<ZUser> {
	private Grid<ZUser> grid;
	private ListStore<ZUser> store;
	private PagingLoader<PagingLoadConfig, PagingLoadResult<ZUser>> loader;
	private PagingToolBar toolBar;
	private HorizontalLayoutContainer toolArea;
	private TextField query_text;
	private TextButton btn_checkpass;
	private TextButton btn_checknotpass;
	private TextButton btn_edit;
	private TextButton btn_cancel;
	private TextButton btn_query;
	private TextButton btn_return;
	private TextButton btn_refresh;

	private static ZUserPropertiesAction prop = GWT
			.create(ZUserPropertiesAction.class);

	public CardCheckUser(final UserPresenter presenter) {
		ColumnConfig<ZUser, String> isEffective = new ColumnConfig<ZUser, String>(
				prop.isEffective(), 30, "用户状态");
		ColumnConfig<ZUser, String> userType = new ColumnConfig<ZUser, String>(
				prop.userType(), 30, "用户类型");
		ColumnConfig<ZUser, String> uid = new ColumnConfig<ZUser, String>(
				prop.uid(), 30, "用户账号");
		ColumnConfig<ZUser, String> name = new ColumnConfig<ZUser, String>(
				prop.name(), 30, "用户姓名");
		ColumnConfig<ZUser, String> email = new ColumnConfig<ZUser, String>(
				prop.email(), 60, "用户邮箱");
		ColumnConfig<ZUser, String> phone = new ColumnConfig<ZUser, String>(
				prop.phone(), 60, "手机号码");
		phone.setCell(new AbstractCell<String>() {

			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				if (value!=null) {
					sb.appendHtmlConstant(UserUtil.getHtmlString(value));
				}else {
					sb.appendHtmlConstant("");
				}
			}
		});
		ColumnConfig<ZUser, String> description = new ColumnConfig<ZUser, String>(
				prop.description(), 60, "描述");
		description.setCell(new AbstractCell<String>() {

			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				if (value!=null) {
					sb.appendHtmlConstant(UserUtil.getHtmlString(value));
				}else {
					sb.appendHtmlConstant("");
				}
			}
		});
		ColumnConfig<ZUser, String> gmtModified = new ColumnConfig<ZUser, String>(
				prop.gmtModified(), 30, "更新日期");
		ColumnModel cm = new ColumnModel(Arrays.asList(isEffective, userType,
				uid, name, email, phone, description, gmtModified));
		RpcProxy<PagingLoadConfig, PagingLoadResult<ZUser>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<ZUser>>() {

			@Override
			public void load(PagingLoadConfig loadConfig,
					AsyncCallback<PagingLoadResult<ZUser>> callback) {
				PagingLoadConfig config = (PagingLoadConfig) loadConfig;
				String filter = query_text.getValue();
				RPCS.getUserService().getUsersPaging(config, filter, callback);
			}

		};

		store = new ListStore<ZUser>(new ModelKeyProvider<ZUser>() {
			public String getKey(ZUser item) {
				return item.getUid();
			}
		});
		loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<ZUser>>(proxy);
		loader.setLimit(50);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, ZUser, PagingLoadResult<ZUser>>(
				store));
		grid = new Grid<ZUser>(store, cm);
		grid.setLoadMask(true);
		grid.getView().setForceFit(true);
		toolBar = new PagingToolBar(50);
		toolBar.bind(loader);
		query_text = new TextField();
		query_text.setWidth(280);
		query_text.setEmptyText("请输入用户账号、用户姓名或者用户邮箱");
		query_text.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					String filter = query_text.getText();
					query_text.setValue(filter);
					load();
				}
			}
		});
		
		btn_return = new TextButton("返回", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				presenter.display(presenter.getZuser());
			}
		});
		btn_edit = new TextButton("编辑", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				ZUser zu = grid.getSelectionModel().getSelectedItem();
				if (zu != null) {
					CardEditUser edit = new CardEditUser(presenter, zu);
					edit.setCallback(new AbstractAsyncCallback<ZUser>() {
						
						@Override
						public void onSuccess(ZUser result) {
							presenter.displayCheckUser();
						}
					});
					edit.show();
				}else {
					AlertMessageBox alert=new AlertMessageBox("警告", "请选中一条记录");
					alert.show();
				}
			};
		});
		
		btn_cancel = new TextButton("删除",new SelectHandler(){

			@Override
			public void onSelect(SelectEvent event) {
				List<ZUser> zulst = grid.getSelectionModel().getSelectedItems();
				if (zulst != null && zulst.size()>0) {
					final List<String> uids = new ArrayList<String>();
					for(ZUser zu : zulst){
						uids.add(zu.getUid());
					}
					ConfirmMessageBox box = new ConfirmMessageBox(
							"删除", "你确认删除选中记录?");
					box.addHideHandler(new HideHandler() {
						public void onHide(HideEvent event) {
							Dialog btn = (Dialog) event.getSource();
							if (btn.getHideButton().getText()
									.equalsIgnoreCase("yes")) {
								grid.mask("操作中，请稍后...");
								RPCS.getUserService().delete(uids, new AbstractAsyncCallback<Void>() {
									@Override
									public void onSuccess(Void result) {
										load();
										grid.unmask();
										Info.display("删除", "操作成功");
									}
									@Override
									public void onFailure(Throwable caught) {
										super.onFailure(caught);
										load();
										grid.unmask();
									}
								});
							}
						}
					});
					box.show();
				}else {
					AlertMessageBox alert=new AlertMessageBox("警告", "请选中至少一条记录");
					alert.show();
				}
				
			}
		});
		
		btn_checkpass = new TextButton("审核通过",new SelectHandler(){

			@Override
			public void onSelect(SelectEvent event) {
				List<ZUser> zulst = grid.getSelectionModel().getSelectedItems();
				if (zulst != null && zulst.size()>0) {
					final List<String> uids = new ArrayList<String>();
					for(ZUser zu : zulst){
						uids.add(zu.getUid());
					}
					ConfirmMessageBox box = new ConfirmMessageBox(
							"审核", "确定选中的记录通过了审核?");
					box.addHideHandler(new HideHandler() {
						public void onHide(HideEvent event) {
							Dialog btn = (Dialog) event.getSource();
							if (btn.getHideButton().getText()
									.equalsIgnoreCase("yes")) {
								grid.mask("操作中，请稍后...");
								RPCS.getUserService().checkpass(uids, new AbstractAsyncCallback<Void>() {
									@Override
									public void onSuccess(Void result) {
										load();
										grid.unmask();
										Info.display("审核通过", "操作成功");
									}
									@Override
									public void onFailure(Throwable caught) {
										super.onFailure(caught);
										load();
										grid.unmask();
									}
								});
							}
						}
					});
					box.show();
					
				}else {
					AlertMessageBox alert=new AlertMessageBox("警告", "请选中至少一条记录");
					alert.show();
				}
			}
		});
		
		btn_checknotpass = new TextButton("审核拒绝",new SelectHandler(){

			@Override
			public void onSelect(SelectEvent event) {
				List<ZUser> zulst = grid.getSelectionModel().getSelectedItems();
				if (zulst != null && zulst.size()>0) {
					final List<String> uids = new ArrayList<String>();
					for(ZUser zu : zulst){
						uids.add(zu.getUid());
					}
					ConfirmMessageBox box = new ConfirmMessageBox(
							"审核", "确定选中的记录拒绝审核?");
					box.addHideHandler(new HideHandler() {
						public void onHide(HideEvent event) {
							Dialog btn = (Dialog) event.getSource();
							if (btn.getHideButton().getText()
									.equalsIgnoreCase("yes")) {
								grid.mask("操作中，请稍后...");
								RPCS.getUserService().checknotpass(uids, new AbstractAsyncCallback<Void>() {
									@Override
									public void onSuccess(Void result) {
										load();
										grid.unmask();
										Info.display("审核拒绝", "操作成功");
									}
									@Override
									public void onFailure(Throwable caught) {
										super.onFailure(caught);
										load();
										grid.unmask();
									}
								});
							}
						}
					});
					box.show();
					
				}else {
					AlertMessageBox alert=new AlertMessageBox("警告", "请选中至少一条记录");
					alert.show();
				}
			}
		});
		
		btn_query = new TextButton("查询",new SelectHandler(){

			@Override
			public void onSelect(SelectEvent event) {
				load();
			}
		});
		btn_refresh = new TextButton("刷新",new SelectHandler(){

			@Override
			public void onSelect(SelectEvent event) {
				load();
			}
		});
		
		toolArea=new HorizontalLayoutContainer();
		toolArea.add(query_text,new HorizontalLayoutData(-1,-1,new Margins(5)));
		toolArea.add(btn_query,new HorizontalLayoutData(-1,-1,new Margins(5)));
		
		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.add(toolArea, new VerticalLayoutData(1, 30));
		con.add(grid, new VerticalLayoutData(1, 1));
		con.add(toolBar, new VerticalLayoutData(1, 30));
		setCenter(con);
		addButton(btn_return);
		addButton(btn_edit);
		addButton(btn_checkpass);
		addButton(btn_checknotpass);
		addButton(btn_cancel);
		addButton(btn_refresh);
	}

	@Override
	public void refresh(ZUser t) {
		load();
	}
	
	private void load(){
		PagingLoadConfig config=new PagingLoadConfigBean();
		config.setOffset(0);
		config.setLimit(50);
		loader.load(config);
	}
}