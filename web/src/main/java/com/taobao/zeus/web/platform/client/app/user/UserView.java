package com.taobao.zeus.web.platform.client.app.user;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;

public class UserView extends CardLayoutContainer{
	private UserPresenter presenter;
	private CardUserInfo info;
	private CardCheckUser check;
	
	public UserView(UserPresenter presenter){
		this.presenter = presenter;
		info = new CardUserInfo(presenter);
		add(info);
		if (presenter.getZuser().getUid().equals(UserUtil.admin)) {
			check = new CardCheckUser(presenter);
			add(check);
		}
	}
	
	public void display(){
		setActiveWidget(info);
		info.setHeadingText("用户信息");
		info.refresh(presenter.getZuser());
		info.getCenter().getElement().setScrollTop(0);
	}
	
	public void displayCheckUser(){
		setActiveWidget(check);
		check.refresh(presenter.getZuser());
		check.setHeadingText("审核用户");
	}

	@Override
	public Widget asWidget() {
		display();
		return this;
	}
}