package com.taobao.zeus.web.platform.client.app.user;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;

public class UserView extends CardLayoutContainer{
	private UserPresenter presenter;
	private CardUserInfo info;
	private CardUserManager userManager;
	
	public UserView(UserPresenter presenter){
		this.presenter = presenter;
		info = new CardUserInfo(presenter);
		add(info);
		if (presenter.getZuser().getUid().equals(UserUtil.admin)) {
			userManager = new CardUserManager(presenter);
		//	add(check);
		}
	}
	
	public void display(){
		setActiveWidget(info);
		info.setHeadingText("当前登录用户信息");
		info.refresh(presenter.getZuser());
		info.getCenter().getElement().setScrollTop(0);
	}
	
	public void displayUserManager(){
		setActiveWidget(userManager);
		userManager.refresh(presenter.getZuser());
		userManager.setHeadingText("所有用户管理");
	}

	@Override
	public Widget asWidget() {
		display();
		return this;
	}
}