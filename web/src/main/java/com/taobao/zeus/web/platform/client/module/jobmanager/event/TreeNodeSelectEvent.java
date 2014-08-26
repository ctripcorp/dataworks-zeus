package com.taobao.zeus.web.platform.client.module.jobmanager.event;

import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeSelectEvent.TreeNodeSelectHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class TreeNodeSelectEvent extends GwtEvent<TreeNodeSelectHandler>{

	public interface TreeNodeSelectHandler extends EventHandler{
		public void onSelect(TreeNodeSelectEvent event);
	}

	public static Type<TreeNodeSelectHandler> type=new Type<TreeNodeSelectHandler>();
	
	private String providerKey;
	
	public TreeNodeSelectEvent(String providerKey){
		this.providerKey=providerKey;
	}
	
	
	@Override
	protected void dispatch(TreeNodeSelectHandler handler) {
		handler.onSelect(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<TreeNodeSelectHandler> getAssociatedType() {
		return type;
	}


	public String getProviderKey() {
		return providerKey;
	}


}
