package com.taobao.zeus.web.platform.client.app.user;

import com.google.gwt.user.client.ui.HasWidgets;
import com.taobao.zeus.web.platform.client.util.PlatformContext;
import com.taobao.zeus.web.platform.client.util.Presenter;
import com.taobao.zeus.web.platform.client.util.ZUser;

public class UserPresenter implements Presenter{
	
	private UserView view;;
	private ZUser zuser;
	
	private PlatformContext context;
	public UserPresenter(PlatformContext context, ZUser zuser){
		this.context=context;
		this.zuser = zuser;
		this.view = new UserView(this);
	}

	@Override
	public void go(HasWidgets hasWidgets) {
		hasWidgets.add(view.asWidget());
	}

	@Override
	public PlatformContext getPlatformContext() {
		return context;
	}

	public ZUser getZuser() {
		return zuser;
	}
	
	public void display(ZUser user){
		this.zuser = user;
		view.display();
	}
	
	public void displayCheckUser(){
		view.displayCheckUser();
	}

}