package com.taobao.zeus.web.platform.client.module.filemanager;

import java.util.List;

import com.google.gwt.user.client.Element;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.TreeGridDragSource;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

public class LimitedTreeGridDragSource extends TreeGridDragSource<FileModel> {

	public LimitedTreeGridDragSource(TreeGrid<FileModel> widget) {
		super(widget);
	}

	public void removeSource() {
		getWidget().getTreeStore().remove(select.getData());
	}

	@Override
	protected void onDragStart(DndDragStartEvent event) {
		Element startTarget = event.getDragStartEvent().getStartElement()
				.<Element> cast();
		Tree.TreeNode<FileModel> start = getWidget().findNode(startTarget);
		if (start == null
				|| !getWidget().getTreeView().isSelectableTarget(startTarget)) {
			event.setCancelled(true);
			return;
		}

		List<FileModel> selected = getWidget().getSelectionModel()
				.getSelectedItems();

		if (selected.size() == 0) {
			event.setCancelled(true);
			return;
		}
		if (selected.size() > 1) {
			event.setCancelled(true);
			return;
		}
		FileModel source = selected.get(0);
		if (getWidget().getTreeStore().getRootItems().contains(source)) {
			event.setCancelled(true);
			return;
		}
		super.onDragStart(event);
	}

	private TreeNode<FileModel> select;

	@Override
	protected void onDragDrop(DndDropEvent event) {
		if (event.getOperation() == Operation.MOVE) {
			select = ((List<TreeNode<FileModel>>) event.getData()).get(0);
		}
	}

	public TreeNode<FileModel> getSelect() {
		return select;
	}
}