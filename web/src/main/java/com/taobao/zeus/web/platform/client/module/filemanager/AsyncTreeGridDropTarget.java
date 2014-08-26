package com.taobao.zeus.web.platform.client.module.filemanager;

import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.TreeGridDropTarget;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

public class AsyncTreeGridDropTarget extends TreeGridDropTarget<FileModel> {

	public AsyncTreeGridDropTarget(TreeGrid<FileModel> treeGrid) {
		super(treeGrid);
		setAllowSelfAsSource(true);
		setFeedback(Feedback.APPEND);
	}

	public void proxySuperDragDrop(DndDropEvent event) {
		super.onDragDrop(event);
	}

}
