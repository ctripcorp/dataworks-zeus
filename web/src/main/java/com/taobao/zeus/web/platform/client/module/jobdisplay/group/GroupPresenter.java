package com.taobao.zeus.web.platform.client.module.jobdisplay.group;

import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;

public class GroupPresenter implements Presenter {

	private GroupView groupView=new GroupView(this);
	private GroupModel groupModel;
	
	private PlatformContext context;
	public GroupPresenter(PlatformContext context){
		this.context=context;
	}
	
	public PlatformContext getPlatformContext(){
		return context;
	}
	
	
	public void displayEditGroup(){
		groupView.displayEditGroup();
	}
	
	public void display(GroupModel group){
		this.groupModel=group;
		groupView.display();
	}
	
	public void displayOverall(){
		groupView.displayOverall();
	}
	
	public void displayRunning(){
		groupView.displayRunning();
	}
	public void displayManual(){
		groupView.displayManual();
	}
	
	public GroupModel getGroupModel(){
		return groupModel;
	}
	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(groupView.asWidget());
	}

}
