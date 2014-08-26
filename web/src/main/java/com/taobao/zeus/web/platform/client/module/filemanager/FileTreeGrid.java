package com.taobao.zeus.web.platform.client.module.filemanager;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.taobao.zeus.web.platform.client.util.PlatformBus;
import com.taobao.zeus.web.platform.client.util.PlatformContext;

public class FileTreeGrid extends TreeGrid<FileModel>{

	private PlatformContext context;
	public FileTreeGrid(PlatformContext context,TreeStore<FileModel> store, ColumnModel<FileModel> cm,
			ColumnConfig<FileModel, ?> treeColumn) {
		super(store, cm, treeColumn);
		this.context=context;
	}
	  @Override
	  public boolean isLeaf(FileModel model) {
	    return !model.isFolder();
	  }

	  public void unbind() {
	    if (storeHandlerRegistration != null) {
	      storeHandlerRegistration.removeHandler();
	    }
	  }

	  @Override
	  protected void onClick(Event event) {
	    super.onClick(event);
	    EventTarget eventTarget = event.getEventTarget();
	    if (Element.is(eventTarget)) {
	      FileModel m = store.get(getView().findRowIndex(Element.as(eventTarget)));
	      if (m == null) {
	        getSelectionModel().deselectAll();
	      }
	    }
	  }
	  
	  @Override
	  protected void onDoubleClick(Event e) {
	    super.onDoubleClick(e);
	    if (Element.is(e.getEventTarget())) {
	      int i = getView().findRowIndex(Element.as(e.getEventTarget()));
	      FileModel m = store.get(i);
	      if(m!=null &&!m.isFolder()){
	    	  context.getPlatformBus().fireEvent(new OpenFileEvent(m));
	      }
	    }
	  }
}
