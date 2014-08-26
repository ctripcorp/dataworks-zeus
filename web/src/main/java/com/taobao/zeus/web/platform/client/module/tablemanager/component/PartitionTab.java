package com.taobao.zeus.web.platform.client.module.tablemanager.component;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.PartitionModel;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.PartitionModelProperties;
import com.taobao.zeus.web.platform.client.module.tablemanager.model.TableModel;
import com.taobao.zeus.web.platform.client.module.word.images.Images;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.widget.TitledCell;
import com.taobao.zeus.web.platform.shared.rpc.TableManagerServiceAsync;
/**
 * @author gufei.wzy 2012-9-21
 */
public class PartitionTab implements IsWidget {
	@SuppressWarnings("unused")
	private PlatformContext context;
	private TableInfoPanel panel;
	private ListStore<PartitionModel> store;
	private Grid<PartitionModel> grid;

	private final PartitionModelProperties props = GWT
			.create(PartitionModelProperties.class);

	private final TableManagerServiceAsync tableService = RPCS
			.getTableManagerService();

	public PartitionTab(TableInfoPanel p) {
		this.panel = p;
		this.context = p.getContext();
	}

	@Override
	public Widget asWidget() {
	    if(grid==null){
    		ColumnConfig<PartitionModel, String> name = new ColumnConfig<PartitionModel, String>(
    				props.name(), 100, "名称");
    		name.setCell(new TitledCell());
    		ColumnConfig<PartitionModel, String> path = new ColumnConfig<PartitionModel, String>(
    				props.path(), 40, "路径");
    		path.setCell(new TitledCell());
    		ColumnConfig<PartitionModel, String> size = new ColumnConfig<PartitionModel, String>(
    				props.size(), 60, "大小");
    		size.setCell(new TitledCell() {
    			@Override
    			public void render(com.google.gwt.cell.client.Cell.Context context,
    					String value, SafeHtmlBuilder sb) {
    				if (value == null || value.equals("")) {
    					sb.appendHtmlConstant("Loading...");
    				} else {
    					super.render(context, value, sb);
    				}
    			}
    		});
            ColumnConfig<PartitionModel, String> download = new ColumnConfig<PartitionModel, String>(
                    new com.sencha.gxt.core.client.ValueProvider<PartitionModel, String>(){
                        @Override
                        public String getValue(PartitionModel object){
                            return null;
                        }
                        @Override
                        public void setValue(PartitionModel object, String value){
                        }
                        @Override
                        public String getPath(){
                            return null;
                        }
                    } , 30, "下载");
            final TextButtonCell downloadButton = new TextButtonCell();
            downloadButton.setIcon(Images.getImageResources().download());
            downloadButton.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    downloadSelectPartition();
                }
            });
            download.setCell(downloadButton);
    		List<ColumnConfig<PartitionModel, ?>> list = new ArrayList<ColumnConfig<PartitionModel, ?>>();
    		list.add(name);
    		list.add(path);
    		list.add(size);
    		list.add(download);
    		ColumnModel<PartitionModel> colModel = new ColumnModel<PartitionModel>(
    				list);
    		grid = new Grid<PartitionModel>(getStore(), colModel);
    		grid.setAllowTextSelection(true);
    		grid.getView().setForceFit(true);
    		grid.getView().setAutoFill(true);
    
    		// 名称和路径字段加上文本框，方便复制
    		final GridEditing<PartitionModel> editing = new GridInlineEditing<PartitionModel>(
    				grid);
    		editing.addEditor(path, new TextField());
    		editing.getEditor(path).setReadOnly(true);
    		editing.addEditor(name, new TextField());
    		editing.getEditor(name).setReadOnly(true);
    
    		// 选择时加载分区数据预览
    		GridSelectionModel<PartitionModel> gs = new GridSelectionModel<PartitionModel>();
    		gs.addSelectionHandler(new SelectionHandler<PartitionModel>() {
    			@Override
    			public void onSelection(SelectionEvent<PartitionModel> event) {
    				panel.getPresenter().loadDataPreview(event.getSelectedItem());
    			}
    		});
    		grid.setSelectionModel(gs);
	    }

		return grid;
	}

    private void downloadSelectPartition() {
        PartitionModel pm = grid.getSelectionModel().getSelectedItem();
        if (RootPanel.get("downloadiframe") != null) {
            Widget widgetFrame = (Widget) RootPanel
                    .get("downloadiframe");
            widgetFrame.removeFromParent();
        }
        Frame frame = new Frame(GWT.getHostPageBaseURL()
                + "partition_download.do" + "?path=" + pm.getPath()
                + "&table=" + panel.getTable().getName());
        frame.setVisible(false);
        frame.setSize("0px", "0px");
        RootPanel.get().add(frame);
    }

	public void load(final TableModel t) {
		if (t == null)
			return;
		grid.mask("加载中...");
		tableService.getPartitions(t,
				new AbstractAsyncCallback<List<PartitionModel>>() {

					@Override
					public void onSuccess(List<PartitionModel> result) {
						getStore().clear();
						getStore().addAll(result);
						grid.unmask();

						// 读取分区大小
						for (PartitionModel p : result) {
							tableService
									.fillPartitionSize(
											p,
											new AbstractAsyncCallback<PartitionModel>() {
												@Override
												public void onSuccess(
														PartitionModel result) {
													getStore().update(result);
												}
											});
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						grid.unmask();
					}
				});
	}

	private ListStore<PartitionModel> getStore() {
		if (store == null) {
			store = new ListStore<PartitionModel>(
					new ModelKeyProvider<PartitionModel>() {
						@Override
						public String getKey(PartitionModel item) {
							return item.getName();
						}
					});
		}
		return store;
	}
}
