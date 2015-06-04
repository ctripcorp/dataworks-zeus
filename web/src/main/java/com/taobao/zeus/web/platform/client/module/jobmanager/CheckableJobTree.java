package com.taobao.zeus.web.platform.client.module.jobmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;
import com.taobao.zeus.web.platform.client.module.jobmanager.images.Images;
import com.taobao.zeus.web.platform.client.util.Callback;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class CheckableJobTree extends Window{
	
	public CheckableJobTree(){
		setHeadingText("选择依赖任务(可以多选)");
		setModal(true);
		setHeight(600);
		setWidth(500);
		add(getLayoutContainer());
		
		store=new TreeStore<GroupJobTreeModel>(TreeKeyProviderTool.getModelKeyProvider());
		tree=new Tree<GroupJobTreeModel, String>(store, new ValueProvider<GroupJobTreeModel, String>() {
			@Override
			public String getPath() {
				return "name";
			}
			@Override
			public String getValue(GroupJobTreeModel object) {
				return object.getName()+"("+object.getId()+")";
			}

			@Override
			public void setValue(GroupJobTreeModel object, String value) {
				
			}
		}){
			//重写onFilter方法，过滤后保留check状态
			//added by gufei.wzy
			@Override
			protected void onFilter(StoreFilterEvent<GroupJobTreeModel> se) {
				// 获取checked状态的node
				List<GroupJobTreeModel> checked = tree.getCheckedSelection();
				super.onFilter(se);
				// 获取过滤后的所有node
				List<GroupJobTreeModel> filtered = new ArrayList<GroupJobTreeModel>();
				if(store.getRootItems().size()==1) {
					//有root节点
					addChildren(filtered, store.getRootItems().get(0));
				}
				// checked的node和过滤后的node取交集
				checked.retainAll(filtered);
				
				tree.setCheckedSelection(checked);
				for(GroupJobTreeModel m : checked){
					tree.setExpanded(m, true);
				}
			};
			
			private void addChildren(List<GroupJobTreeModel> list,List<GroupJobTreeModel> pl){
				for(GroupJobTreeModel p : pl){
					if(store.getChildren(p)!=null && !store.getChildren(p).isEmpty()){
						addChildren(list, p.getChildren());
					}
					list.add(p);
				}
			}
			/**
			 * 递归获取所有子节点，结果包括本节点
			 * @param list
			 * @param p
			 */
			private void addChildren(List<GroupJobTreeModel> list, GroupJobTreeModel p){
				
				if(store.getChildren(p)!=null && !store.getChildren(p).isEmpty()){
					addChildren(list, p.getChildren());
				}
				list.add(p);
			}
		};
		tree.setCheckable(true);
		tree.setCheckStyle(CheckCascade.CHILDREN);
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
		filter=new StoreFilterField<GroupJobTreeModel>(){
			@Override
			protected boolean doSelect(Store<GroupJobTreeModel> store,
					GroupJobTreeModel parent, GroupJobTreeModel item,
					String filter) {
				filter=filter.trim();
				if(filter.contains(" ")){
					String[] cs=filter.split(" ");
					for(String c:cs){
						// 有单引号，精确匹配id
						if (hasComma(c)){
							if(item.isJob() && item.getId().equals(c.substring(1,c.length()-1))) {
								return true;
							}
						}else{
						// 没有单引号，非精确匹配
							if(item.getId().equals(c)){
								return true;
							}
							if (item.getName().toLowerCase().contains(c.toLowerCase())) {
								return true;
							}
						}
					}
				}else{
					// 有单引号，精确匹配id
					if (hasComma(filter)){
						if(item.isJob() && item.getId().equals(filter.substring(1,filter.length()-1))) {
							return true;
						}
					}else{
					// 没有单引号，非精确匹配
						if(item.getId().equals(filter)){
							return true;
						}
						if (item.getName().toLowerCase().contains(filter.toLowerCase())) {
							return true;
						}
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
					tree.collapseAll();
					tree.getSelectionModel().deselect(model);
					tree.setExpanded(model, true);
					tree.getSelectionModel().select(model, true);
				}
			}
			
		};
		filter.setEmptyText("id 或者 名称 匹配搜索,使用空格匹配多个，使用单引号（如'123'）进行精确匹配");
		filter.bind(tree.getStore());
		getLayoutContainer().add(filter,new VerticalLayoutContainer.VerticalLayoutData(1, 30, new Margins(4)));
		getLayoutContainer().add(tree,new VerticalLayoutContainer.VerticalLayoutData(1, 1d, new Margins(4)));
		
		addButton(new TextButton("确定", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(handler!=null){
					handler.onSelect(event);
				}
				CheckableJobTree.this.hide();
			}
		}));
		refresh();
	}

	private boolean hasComma(String s){
		return s!=null&&s.length()>=3&&s.startsWith("'")&&s.endsWith("'");
	}
	private SelectHandler handler;
	public void setSelectHandler(SelectHandler handler){
		this.handler=handler;
	}
	private VerticalLayoutContainer container;
	private Tree<GroupJobTreeModel, String> tree;
	private StoreFilterField<GroupJobTreeModel> filter;
	public Tree<GroupJobTreeModel, String> getTree() {
		return tree;
	}
	public VerticalLayoutContainer getLayoutContainer(){
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
		List<GroupJobTreeModel> children=parent.getChildren();
		for(int i=0;i<children.size();i++){
			GroupJobTreeModel child=children.get(i);
			store.add(parent, child);
			rel(store,child);
		}
	}
	
	private TreeStore<GroupJobTreeModel> store;
	public void refresh(){
		refresh(null);
	}
	public void refresh(final Callback callback){
		RPCS.getTreeService().getTreeData(new AbstractAsyncCallback<GroupJobTreeModel>() {
			@Override
			public void onSuccess(GroupJobTreeModel result) {
				for(GroupJobTreeModel m:store.getRootItems()){
					store.remove(m);
				}
				parser(store, result);
				tree.setExpanded(result, true);
				if(callback!=null){
					callback.callback();
				}
			}
		});
		
	}
	public void init(String curValue){
		tree.getSelectionModel().deselectAll();
		StringBuffer filterText = new StringBuffer();
		if(curValue!=null&&!curValue.isEmpty()){
			List<String> curDeps = Arrays.asList(curValue.split(","));
			for(GroupJobTreeModel m: store.getAll()){
				if(m.isJob()&&curDeps.contains(m.getId())){
					tree.setChecked(m, CheckState.CHECKED);
					tree.setExpanded(m, true);
				}
			}
			boolean first=true;
			for(String s:curDeps){
				if(!first){
					filterText.append(' ');
				}else{
					first = false;
				}
				filterText.append('\'').append(s).append('\'');
			}
			filter.setText(filterText.toString());
		}
	}
}
