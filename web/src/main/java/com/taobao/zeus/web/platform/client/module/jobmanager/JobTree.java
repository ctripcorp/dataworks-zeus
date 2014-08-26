package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.dnd.core.client.TreeDropTarget;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeChangeEvent;
import com.taobao.zeus.web.platform.client.module.jobmanager.images.Images;
import com.taobao.zeus.web.platform.client.util.Callback;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class JobTree extends ContentPanel{
	
	private JobManagerPresenter presenter;
	public JobTree(final JobManagerPresenter presenter,boolean isMy){
		super((ContentPanelAppearance) GWT.create(AccordionLayoutAppearance.class));
		this.isMy=isMy;
		this.presenter=presenter;
		add(getContainer());
		refresh();
	}
	private VerticalLayoutContainer container;
	private final boolean isMy;
	private Tree<GroupJobTreeModel, String> tree;
	private StoreFilterField<GroupJobTreeModel> filter;
	public Tree<GroupJobTreeModel, String> getTree() {
		return tree;
	}
	public VerticalLayoutContainer getContainer(){
		if(container==null){
			container=new VerticalLayoutContainer();
		}
		return container;
	}
	private void parser(TreeStore<GroupJobTreeModel> store,GroupJobTreeModel root){
		store.add(root);
		rel(store,root);
	}
	private void rel(TreeStore<GroupJobTreeModel> store,GroupJobTreeModel parent){
		List children=parent.getChildren();
		for(int i=0;i<children.size();i++){
			GroupJobTreeModel child=(GroupJobTreeModel) children.get(i);
			store.add(parent, child);
			rel(store,child);
		}
	}
	private TreeStore<GroupJobTreeModel> store;
	public void refresh(){
		refresh(null);
	}
	private boolean loading;
	public void refresh(final Callback callback){
		if(tree==null){
			store=new TreeStore<GroupJobTreeModel>(TreeKeyProviderTool.getModelKeyProvider());
			tree=new Tree<GroupJobTreeModel, String>(store, new ValueProvider<GroupJobTreeModel, String>() {
				public String getPath() {
					return "name";
				}
				public String getValue(GroupJobTreeModel object) {
					String name="";
					name+=object.getName()+"("+object.getId()+")";
					if(object.isFollow()){
						name+="(关注)" ;
					}
					return name;
				}

				@Override
				public void setValue(GroupJobTreeModel object, String value) {
					
				}
			});
			tree.setIconProvider(new IconProvider<GroupJobTreeModel>() {
				@Override
				public ImageResource getIcon(GroupJobTreeModel model) {
					if(model.isJob()){
						return Images.getResources().job();
					}else if(model.isDirectory()){
						return Images.getResources().folder_group();
					}else{
						return Images.getResources().leaf_group();
					}
				}
			});
			tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<GroupJobTreeModel>() {
				public void onSelectionChanged(
						SelectionChangedEvent<GroupJobTreeModel> event) {
					if(!loading){
						if(!event.getSelection().isEmpty()){
							GroupJobTreeModel model=event.getSelection().get(0);
							presenter.onSelect(TreeKeyProviderTool.getModelKeyProvider().getKey(model));
						}
					}
				}
			});
			Menu menu=new Menu();
			final MenuItem follow = new MenuItem("关注(接收报警)",
					new SelectionHandler<MenuItem>() {
						@Override
						public void onSelection(SelectionEvent<MenuItem> event) {
							final GroupJobTreeModel model = tree.getSelectionModel().getSelectedItem();
							int type = 0;
							if (model.isGroup()) {
								type = 1;
							} else if (model.isJob()) {
								type = 2;
							}
							RPCS.getTreeService().follow(type,
									model.getId(),
									new AbstractAsyncCallback<Void>() {
										public void onSuccess(Void result) {
											model.setFollow(true);
											tree.refresh(model);
											Info.display("成功", "关注成功");
										}
									});
								}
					});
			final MenuItem unfollow = new MenuItem("取消关注",
					new SelectionHandler<MenuItem>() {
						@Override
						public void onSelection(SelectionEvent<MenuItem> event) {
							final GroupJobTreeModel model = tree
									.getSelectionModel().getSelectedItem();
							int type = 0;
							if (model.isGroup()) {
								type = 1;
							} else if (model.isJob()) {
								type = 2;
							}
							RPCS.getTreeService().unfollow(type,
									model.getId(),
									new AbstractAsyncCallback<Void>() {
										public void onSuccess(Void result) {
											model.setFollow(false);
											tree.refresh(model);
											Info.display("成功", "取消关注成功");
										}
									});
						}
					});
			menu.add(follow);
			menu.add(unfollow);
			tree.setContextMenu(menu);
			tree.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {
				public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
					GroupJobTreeModel model=tree.getSelectionModel().getSelectedItem();
					if(model!=null){
						if(model.isFollow()){
							follow.hide();
							unfollow.show();
						}else{
							follow.show();
							unfollow.hide();
						}
					}
				}
			});
			filter=new StoreFilterField<GroupJobTreeModel>(){
				protected boolean doSelect(Store<GroupJobTreeModel> store,
						GroupJobTreeModel parent, GroupJobTreeModel item,
						String filter) {
					filter=filter.trim();
					if(filter.contains(" ")){
						String[] cs=filter.split(" ");
						for(String c:cs){
							if (item.getId().equals(c)) {
								return true;
							}
							if (item.getName().contains(c)) {
								return true;
							}
						}
					}else{
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
					GroupJobTreeModel model=tree.getSelectionModel().getSelectedItem();
					setText("");
					onFilter();
					if(model==null){
						tree.collapseAll();
						tree.setExpanded(tree.getStore().getRootItems().get(0), true);
					}else{
						System.out.println(model.getName());
						tree.collapseAll();
						tree.getSelectionModel().deselect(model);
						tree.setExpanded(model, true);
						tree.getSelectionModel().select(model, true);
					}
				}
				@Override
				protected void applyFilters(Store<GroupJobTreeModel> store) {
					tree.getSelectionModel().deselectAll();
					super.applyFilters(store);
				}
			};
			filter.setEmptyText("id 或者 名称 匹配搜索,使用空格可匹配多个");
			filter.bind(tree.getStore());
			getContainer().add(filter,new VerticalLayoutContainer.VerticalLayoutData(1, 30, new Margins(4)));
			getContainer().add(tree,new VerticalLayoutContainer.VerticalLayoutData(1, 1d, new Margins(4)));
			
			//拖拽
			if(!isMy){
				JobDragSource source=new JobDragSource(tree);
				JobDropTarget target=new JobDropTarget(tree, source);
				target.setAllowDropOnLeaf(true);
				target.setAllowSelfAsSource(true);
				target.setFeedback(Feedback.APPEND);
			}
		}
		
		if(isMy){
			loading=true;
			RPCS.getTreeService().getMyTreeData(new AbstractAsyncCallback<GroupJobTreeModel>(){
				public void onSuccess(GroupJobTreeModel result) {
					loading=false;
					for(GroupJobTreeModel m:store.getRootItems()){
						store.remove(m);
					}
					parser(store, result);
					tree.setExpanded(result, true);
					if(callback!=null){
						callback.callback();
					}
				}
				@Override
				public void onFailure(Throwable caught) {
					loading=false;
					super.onFailure(caught);
				}
			});
		}else{
			loading=true;
			RPCS.getTreeService().getTreeData(new AbstractAsyncCallback<GroupJobTreeModel>() {
				public void onSuccess(GroupJobTreeModel result) {
					loading=false;
					for(GroupJobTreeModel m:store.getRootItems()){
						store.remove(m);
					}
					parser(store, result);
					tree.setExpanded(result, true);
					if(callback!=null){
						callback.callback();
					}
				}
				@Override
				public void onFailure(Throwable caught) {
					loading=false;
					super.onFailure(caught);
				}
			});
		}
		
	}
	public class JobDropTarget extends TreeDropTarget<GroupJobTreeModel> {

		private JobDragSource source;
		public JobDropTarget(Tree<GroupJobTreeModel,String> tree,JobDragSource source) {
			super(tree);
			this.source=source;
		}
		protected void onDragDrop(final DndDropEvent event) {
			if (activeItem == null || activeItem.getModel() == null) {
				return;
			}
			final GroupJobTreeModel sourceItem=source.getSelect().getData();
			if(sourceItem.isGroup()){
				if(!activeItem.getModel().isDirectory()){
					Info.display("失败", "非法的操作");
					return;
				}
			}else{
				if(activeItem.getModel().isDirectory()){
					Info.display("失败", "非法的操作");
					return;
				}
			}
			final JobDropTarget targetThis=this;
			ConfirmMessageBox confirm=new ConfirmMessageBox("警告", "迁移组/任务将会产生一定的风险，请确认:<br/>1.本次迁移造成的继承配置项改变，不影响任务正常运行.<br/>2.本次迁移造成的继承资源改变，不影响任务正常运行");
			confirm.addHideHandler(new HideHandler() {
				public void onHide(HideEvent he) {
					Dialog dialog=(Dialog) he.getSource();
					if(DefaultMessages.getMessages().messageBox_yes().equals(dialog.getHideButton().getText())){
						if(sourceItem.isJob()){
							RPCS.getJobService().move(sourceItem.getId(), activeItem.getModel().getId(), new AbstractAsyncCallback<Void>() {
								public void onSuccess(Void result) {
									source.removeSource();
									targetThis.proxySuperDragDrop(event);
									TreeNodeChangeEvent tnce=new TreeNodeChangeEvent();
									tnce.setNeedSelectProviderKey(TreeKeyProviderTool.genJobProviderKey(sourceItem.getId()));
									presenter.getPlatformContext().getPlatformBus().fireEvent(tnce);
								}
							});
						}else{
							RPCS.getGroupService().move(sourceItem.getId(), activeItem.getModel().getId(), new AbstractAsyncCallback<Void>() {
								public void onSuccess(Void result) {
									source.removeSource();
									targetThis.proxySuperDragDrop(event);
									TreeNodeChangeEvent tnce=new TreeNodeChangeEvent();
									tnce.setNeedSelectProviderKey(TreeKeyProviderTool.genGroupProviderKey(sourceItem.getId()));
									presenter.getPlatformContext().getPlatformBus().fireEvent(tnce);
								}
							});
						}
					}
				}
			});
			confirm.show();
			return ;
		}
		public void proxySuperDragDrop(DndDropEvent event) {
			super.onDragDrop(event);
		}

	}
	public class JobDragSource extends TreeDragSource<GroupJobTreeModel>{
		public JobDragSource(Tree<GroupJobTreeModel, String> tree) {
			super(tree);
		}
		@Override
		protected void onDragStart(DndDragStartEvent event) {
			List<GroupJobTreeModel> selected = getWidget().getSelectionModel().getSelectedItems();

			if (selected.size() == 0) {
				event.setCancelled(true);
				return;
			}
			if (selected.size() > 1) {
				event.setCancelled(true);
				return;
			}
			GroupJobTreeModel source = selected.get(0);
			if (getWidget().getStore().getRootItems().contains(source)) {
				event.setCancelled(true);
				return;
			}
			super.onDragStart(event);
		}
		private TreeNode<GroupJobTreeModel> select;

		@Override
		protected void onDragDrop(DndDropEvent event) {
			if (event.getOperation() == Operation.MOVE) {
				select = ((List<TreeNode<GroupJobTreeModel>>) event.getData()).get(0);
			}
		}

		public TreeNode<GroupJobTreeModel> getSelect() {
			return select;
		}
		public void removeSource() {
			getWidget().getStore().remove(select.getData());
		}
	}
}
