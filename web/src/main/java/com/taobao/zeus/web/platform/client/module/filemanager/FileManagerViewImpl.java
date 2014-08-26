package com.taobao.zeus.web.platform.client.module.filemanager;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent.CancelEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.taobao.zeus.web.platform.client.module.filemanager.images.Images;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.util.async.PlatformAsyncCallback;
import com.taobao.zeus.web.platform.client.util.filesystem.FSUtil;
import com.taobao.zeus.web.platform.shared.rpc.FileClientBean;
import com.taobao.zeus.web.platform.shared.rpc.FileManagerService;
import com.taobao.zeus.web.platform.shared.rpc.FileManagerServiceAsync;

public class FileManagerViewImpl implements FileManagerView {

	private FileManagerPresenter fileManagerPresenter;
	private PlatformContext context;
	private TreeStore<FileModel> myTreeStore;
	private FileTreeGrid myTreeGrid;
	private FileTreeGrid commonTreeGrid;
	private FileModel editFileModel;
	private FileManagerGridInlineEditing gridEditing;
	private CompleteEditHandler<FileModel> completeEditHandler;
	private CancelEditHandler<FileModel> cancelEditHandler;

	private AccordionLayoutContainer container;
	@UiField
	ContentPanel myDoc;
	@UiField
	ContentPanel commonDoc;

	private static FileManagerViewImplUiBinder uiBinder = GWT
			.create(FileManagerViewImplUiBinder.class);

	interface FileManagerViewImplUiBinder extends
			UiBinder<AccordionLayoutContainer, FileManagerViewImpl> {
	}

	public FileManagerViewImpl(final PlatformContext context,
			FileManagerPresenter presenter) {
		this.fileManagerPresenter = presenter;
		this.context = context;
		container = uiBinder.createAndBindUi(this);
		myDoc.add(getMyTreeGrid());
		
		
		VerticalLayoutContainer commonContainer=new VerticalLayoutContainer();
		commonContainer.setScrollMode(ScrollMode.AUTOY);
		commonContainer.add(getCommonDocFilter(),
				new VerticalLayoutData(1, 30, new Margins(4, 20, 4, 4)));
		commonContainer.add(getCommonTreeGrid(), new VerticalLayoutData(1, 1));
		commonDoc.add(commonContainer);
		
		container.setActiveWidget(myDoc);
		
	}

	private StoreFilterField<FileModel> filter;

	private StoreFilterField<FileModel> getCommonDocFilter() {
		if (filter == null) {
			filter = new StoreFilterField<FileModel>() {
				@Override
				protected boolean doSelect(Store<FileModel> store,
						FileModel parent, FileModel item, String filter) {
					filter = filter.trim();
					if (filter.contains(" ")) {
						String[] cs = filter.split(" ");
						for (String c : cs) {
							if (item.getId().equals(c)) {
								return true;
							}
							if (item.getName().contains(c)) {
								return true;
							}
						}
					} else {
						if (item.getId().equals(filter)) {
							return true;
						}
						if (item.getName().contains(filter)) {
							return true;
						}
					}
					return false;
				}

				@Override
				protected void onTriggerClick(TriggerClickEvent event) {
					FileModel model = getCommonTreeGrid().getSelectionModel()
							.getSelectedItem();
					setText("");
					onFilter();
					if (model == null) {
						getCommonTreeGrid().collapseAll();
					} else {
						getCommonTreeGrid().collapseAll();
						getCommonTreeGrid().getSelectionModel().deselect(model);
						getCommonTreeGrid().setExpanded(model, true);
						getCommonTreeGrid().getSelectionModel().select(model,
								true);
					}
				}

			};
			filter.setEmptyText("名称 或者 域账号  匹配搜索,使用空格可匹配多个");
			filter.bind(getCommonTreeGrid().getStore());
		}
		return filter;
	}

	@UiFactory
	public ContentPanel createContentPanel(ContentPanelAppearance appearance) {
		return new ContentPanel(appearance);
	}

	@Override
	public void collapse() {
		FileModel fileModel = getMyTreeGrid().getSelectionModel()
				.getSelectedItem();
		if (fileModel == null) {
			getMyTreeGrid().collapseAll();
		} else {
			getMyTreeGrid().setExpanded(fileModel, false, true);
		}
	}

	@Override
	public void editName(FileModel fileModel) {
		editSaveFileModel(fileModel);
		Element row = getMyTreeGrid().getView().getRow(fileModel);
		int rowIndex = getMyTreeGrid().getView().findRowIndex(row);
		getGridEditing().startEditing(new GridCell(rowIndex, 0));
	}

	private void editSaveFileModel(FileModel editFileModel) {
		this.editFileModel = editFileModel;
	}

	@Override
	public void expand() {
		FileModel fileModel = getMyTreeGrid().getSelectionModel()
				.getSelectedItem();
		if (fileModel == null) {
			getMyTreeGrid().expandAll();
		} else {
			getMyTreeGrid().setExpanded(fileModel, true, true);
		}
	}

	@Override
	public FileModel getSelectedItem() {
		return getMyTreeGrid().getSelectionModel().getSelectedItem();
	}

	@Override
	public List<FileModel> getSelectedItems() {
		return getMyTreeGrid().getSelectionModel().getSelectedItems();
	}

	@Override
	public void selectFileModel(FileModel fileModel) {
		getMyTreeGrid().setExpanded(fileModel, true);
		getMyTreeGrid().getSelectionModel().select(fileModel, false);
	}

	@Override
	public Widget asWidget() {
		return container;
	}

	public FileManagerGridInlineEditing getGridEditing() {
		if (gridEditing == null) {
			gridEditing = new FileManagerGridInlineEditing(getMyTreeGrid());
			gridEditing.setClicksToEdit(null);
			gridEditing.addEditor(getNameConfig(), gridEditing.getTextField());
			gridEditing.addCompleteEditHandler(getCompleteEditHandler());
			gridEditing.addCancelEditHandler(getCancelEditHandler());
		}
		return gridEditing;
	}

	public CompleteEditHandler<FileModel> getCompleteEditHandler() {
		if (completeEditHandler == null) {
			completeEditHandler = new CompleteEditHandler<FileModel>() {
				@Override
				public void onCompleteEdit(CompleteEditEvent<FileModel> event) {
					editRestoreFileModel();
					getFileManagerPresenter().onEditFileNameComplete(
							editFileModel);
					// Give the change a chance to propagate to model and store
					// Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
					// {
					// @Override
					// public boolean execute() {
					// getWindow().setHeadingText(getTitle(getSelectedItem()));
					// return false;
					// }
					// }, 250);
				}
			};
		}
		return completeEditHandler;
	}

	private void editRestoreFileModel() {
		if (editFileModel != null) {
			getMyTreeGrid().getSelectionModel().select(editFileModel, true);
		}
	}

	public FileManagerPresenter getFileManagerPresenter() {
		return fileManagerPresenter;
	}

	public CancelEditHandler<FileModel> getCancelEditHandler() {
		if (cancelEditHandler == null) {
			cancelEditHandler = new CancelEditHandler<FileModel>() {
				@Override
				public void onCancelEdit(CancelEditEvent<FileModel> event) {
					/*
					 * Works around a minor issue with GridInlineEditing in
					 * which any update operation that does not change the value
					 * is reported as a cancel.
					 */
					if (gridEditing.isEnter()) {
						getFileManagerPresenter().onEditFileNameComplete(
								editFileModel);
					} else {
						getFileManagerPresenter().onEditFileNameComplete(
								editFileModel);
					}
				}
			};
		}
		return cancelEditHandler;
	}

	public FileTreeGrid getCommonTreeGrid() {
		if (commonTreeGrid == null) {
			ColumnConfig<FileModel, String> name = getNameConfig();
			List<ColumnConfig<FileModel, ?>> list = new ArrayList<ColumnConfig<FileModel, ?>>();
			list.add(name);
			ColumnModel<FileModel> cm = new ColumnModel<FileModel>(list);

			final TreeStore<FileModel> commonTreeStore = new TreeStore<FileModel>(
					new ModelKeyProvider<FileModel>() {
						@Override
						public String getKey(FileModel item) {
							return item.getId();
						}
					});
			final FileManagerServiceAsync fileService = com.google.gwt.core.shared.GWT
					.create(FileManagerService.class);
			RpcProxy<FileModel, List<FileModel>> proxy = new RpcProxy<FileModel, List<FileModel>>() {
				@Override
				public void load(FileModel loadConfig,
						AsyncCallback<List<FileModel>> callback) {
					fileService.getCommonFiles(loadConfig, callback);
				}
			};
			final TreeLoader<FileModel> loader = new TreeLoader<FileModel>(
					proxy) {
				@Override
				public boolean hasChildren(FileModel parent) {
					return parent.isFolder();
				}
			};
			loader.addLoadHandler(new ChildTreeStoreBinding<FileModel>(
					commonTreeStore));
			loader.load();

			commonTreeGrid = new FileTreeGrid(context, commonTreeStore, cm,
					name);
			commonTreeGrid.setTreeLoader(loader);
			commonTreeGrid.setHideHeaders(true);
			commonTreeGrid.getView().setForceFit(true);
			commonTreeGrid.setIconProvider(new IconProvider<FileModel>() {
				@Override
				public ImageResource getIcon(FileModel model) {
					if (!model.isFolder()) {
						if (model.getName().endsWith(".txt")) {
							return Images.getImageResources().page_white();
						} else {
							return Images.getImageResources().script();
						}
					}
					return null;
				}
			});

			Menu menu = new Menu();
			MenuItem reload = new MenuItem("重新加载",
					new SelectionHandler<MenuItem>() {
						@Override
						public void onSelection(SelectionEvent<MenuItem> event) {
							FileModel fm = commonTreeGrid.getSelectionModel()
									.getSelectedItem();
							if (fm != null) {
								commonTreeStore.removeChildren(fm);
								loader.loadChildren(fm);
							}
						}
					});
			menu.add(reload);
			final MenuItem referMenuItem = getReferMenuItem(true);
			menu.add(referMenuItem);
			menu.addBeforeShowHandler(new BeforeShowHandler() {
				@Override
				public void onBeforeShow(BeforeShowEvent event) {
					if(getCommonTreeGrid().getSelectionModel().getSelectedItem().isFolder()){
						referMenuItem.setEnabled(false);
					}else{
						referMenuItem.setEnabled(true);
					}
				}
			});
			commonTreeGrid.setContextMenu(menu);

		}
		return commonTreeGrid;
	}

	public FileTreeGrid getMyTreeGrid() {
		if (myTreeGrid == null) {
			ColumnConfig<FileModel, String> name = getNameConfig();
			List<ColumnConfig<FileModel, ?>> list = new ArrayList<ColumnConfig<FileModel, ?>>();
			list.add(name);
			ColumnModel<FileModel> cm = new ColumnModel<FileModel>(list);

			myTreeGrid = new FileTreeGrid(context, getMyTreeStore(), cm, name);
			Menu fileMenu = new FileMenu(fileManagerPresenter)
					.getFileMenu();
			final MenuItem referMenuItem = getReferMenuItem(false);
			fileMenu.add(new SeparatorMenuItem());
			fileMenu.add(referMenuItem);
			fileMenu.addBeforeShowHandler(new BeforeShowHandler() {
				@Override
				public void onBeforeShow(BeforeShowEvent event) {
					if(getMyTreeGrid().getSelectionModel().getSelectedItem().isFolder()){
						referMenuItem.setEnabled(false);
					}else{
						referMenuItem.setEnabled(true);
					}
				}
			});
			myTreeGrid.setContextMenu(fileMenu);
			myTreeGrid.setHideHeaders(true);
			myTreeGrid.getView().setForceFit(true);
			myTreeGrid.setIconProvider(new IconProvider<FileModel>() {
				@Override
				public ImageResource getIcon(FileModel model) {
					if (!model.isFolder()) {
						if (model.getName().endsWith(".txt")) {
							return Images.getImageResources().page_white();
						} else {
							return Images.getImageResources().script();
						}
					}
					return null;
				}
			});

			final LimitedTreeGridDragSource source = new LimitedTreeGridDragSource(
					myTreeGrid);
			AsyncTreeGridDropTarget target = new AsyncTreeGridDropTarget(
					getMyTreeGrid()) {
				@Override
				protected void onDragDrop(final DndDropEvent event) {
					if (activeItem == null || activeItem.getModel() == null) {
						return;
					}
					final AsyncTreeGridDropTarget targetThis = this;
					context.getFileSystem().moveFile(
							source.getSelect().getData().getId(),
							activeItem.getModel().getId(),
							new PlatformAsyncCallback<Void>() {
								@Override
								public void callback(Void t) {
									source.removeSource();
									targetThis.proxySuperDragDrop(event);
								}
							});
					return;
				}
			};
			target.setAllowSelfAsSource(true);
			target.setFeedback(Feedback.APPEND);
		}
		return myTreeGrid;
	}

	private ColumnConfig<FileModel, String> nameConfig;

	public ColumnConfig<FileModel, String> getNameConfig() {
		if (nameConfig == null) {
			nameConfig = new ColumnConfig<FileModel, String>(FSUtil
					.getFileModelProperties().name(), 200, "Name");
		}
		return nameConfig;
	}

	@Override
	public TreeStore<FileModel> getMyTreeStore() {
		if (myTreeStore == null) {
			myTreeStore = new TreeStore<FileModel>(
					new ModelKeyProvider<FileModel>() {
						@Override
						public String getKey(FileModel item) {
							return item.getId();
						}
					});
			myTreeStore.setAutoCommit(true);
			FileManagerServiceAsync fileService = com.google.gwt.core.shared.GWT
					.create(FileManagerService.class);
			fileService
					.getUserFiles(new AbstractAsyncCallback<FileClientBean>() {
						@Override
						public void onSuccess(FileClientBean bean) {
							for (FileClientBean sub : bean.getSubFiles()) {
								myTreeStore.add(sub.getFileModel());
								recursion(sub);
							}
						};

						private void recursion(FileClientBean bean) {
							for (FileClientBean sub : bean.getSubFiles()) {
								myTreeStore.add(bean.getFileModel(),
										sub.getFileModel());
								recursion(sub);
							}
						}
					});
		}
		return myTreeStore;
	}
	


	  private MenuItem getReferMenuItem(final boolean isCommon) {
		  	final MenuItem referMenuItem = new MenuItem("复制引用语句", new SelectionHandler<MenuItem>() {
				@Override
				public void onSelection(SelectionEvent<MenuItem> event) {
					FileModel m =isCommon?getCommonTreeGrid().getSelectionModel().getSelectedItem():getMyTreeGrid().getSelectionModel().getSelectedItem();
					Window w = new Window();
					final TextField f = new TextField(){
						@Override
						public void onBrowserEvent(Event event) {
							super.onBrowserEvent(event);
							selectAll();
						}
					};
					w.setModal(true);
					w.setHeadingText("脚本引用语句");
					w.setHeight(60);
					StringBuffer sb = new StringBuffer();
					sb.append("download[").append("doc://")
						.append(m.getId()).append(' ').append(m.getName().replace(' ', '_'))
						.append("]");
					f.setValue(sb.toString());
					f.setReadOnly(true);
					f.setWidth(350);
					w.setWidget(f);
					w.show();
					f.focus();
				}
			});
	    	referMenuItem.setIcon(Images.getImageResources().script());
	    return referMenuItem;
	  }

	@Override
	public void setMyActivity() {
		container.setActiveWidget(myDoc);
	}

	@Override
	public void setSharedActivity() {
		container.setActiveWidget(commonDoc);
	}
}
