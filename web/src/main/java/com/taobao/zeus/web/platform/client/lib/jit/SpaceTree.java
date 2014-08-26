package com.taobao.zeus.web.platform.client.lib.jit;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupJobTreeModel;

public class SpaceTree extends Component{

	private JavaScriptObject tree;
	
	private String injectInto;
	private String orientation;
	private static Integer id=0;
	private static String getIncreamentId(){
		return "space-tree-"+(++id);
	}
	public SpaceTree(String orientation){
		setElement(DOM.createDiv());
		getElement().setId(getIncreamentId().toString());
		setHeight(400);
		injectInto=getElement().getId();
		this.orientation=orientation;
	}
	private native JavaScriptObject createTree()/*-{
		var tree=$wnd.createTree(this.@com.taobao.zeus.web.platform.client.lib.jit.SpaceTree::injectInto,
			this.@com.taobao.zeus.web.platform.client.lib.jit.SpaceTree::orientation);
		return tree;
	}-*/;
	public void loadData(String jsonData){
		if(tree==null){
			tree=createTree();
		}
		loadData0(jsonData);
	}
	private native void loadData0(String jsonData)/*-{
		var tree=this.@com.taobao.zeus.web.platform.client.lib.jit.SpaceTree::tree;
		$wnd.console.log(jsonData);
		var data=eval("("+jsonData+")");
		tree.loadJSON(data);
		tree.compute();
		tree.onClick(tree.root);
	}-*/;
	
	
}
