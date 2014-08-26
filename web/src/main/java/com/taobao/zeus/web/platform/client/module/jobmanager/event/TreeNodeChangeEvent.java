package com.taobao.zeus.web.platform.client.module.jobmanager.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeChangeEvent.TreeNodeChangeHandler;

public class TreeNodeChangeEvent extends GwtEvent<TreeNodeChangeHandler>{

	public interface TreeNodeChangeHandler extends EventHandler{
		public void onChange(TreeNodeChangeEvent event);
		public void onJobUpdate(JobModel job,TreeNodeChangeEvent event);
		public void onGroupUpdate(GroupModel group,TreeNodeChangeEvent event);
	}
	public static final Type<TreeNodeChangeHandler> TYPE=new Type<TreeNodeChangeHandler>();
	
	private GroupModel group;
	private JobModel job;
	private String needSelectProviderKey;
	public TreeNodeChangeEvent(){
		
	}
	public TreeNodeChangeEvent(GroupModel group){
		this.group=group;
	}
	public TreeNodeChangeEvent(JobModel job){
		this.job=job;
	}
	
	@Override
	protected void dispatch(TreeNodeChangeHandler handler) {
		if(group!=null){
			handler.onGroupUpdate(group,this);
		}else if(job!=null){
			handler.onJobUpdate(job,this);
		}else{
			handler.onChange(this);
		}
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<TreeNodeChangeHandler> getAssociatedType() {
		return TYPE;
	}
	public String getNeedSelectProviderKey() {
		return needSelectProviderKey;
	}
	public void setNeedSelectProviderKey(String needSelectProviderKey) {
		this.needSelectProviderKey = needSelectProviderKey;
	}

	
}
