package com.taobao.zeus.web.platform.client.app.user;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.util.Refreshable;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class CardUserInfo extends CenterTemplate implements Refreshable<ZUser> {

	private UserPresenter presenter;
	private Label luid;
	private Label lname;
	private Label luserType;
	private Label leffective;
	private Label lemail;
	private HTMLPanel phoneContent;
	private HTMLPanel descriptionContent;
	
	private TextButton edituser=new TextButton("编辑",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			CardEditUser edit = new CardEditUser(presenter,presenter.getZuser());
			edit.setCallback(new AbstractAsyncCallback<ZUser>(){

					@Override
					public void onSuccess(ZUser result) {
						presenter.display(result);
					}
			});
			edit.show();
		}
	});
	
	private TextButton checkuser=new TextButton("审核用户",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			presenter.displayCheckUser();
		}
	});
	
	public CardUserInfo(UserPresenter presenter) {
		this.presenter = presenter;
		addButton(edituser);
		if (presenter.getZuser().getUid().equals(UserUtil.admin)) {
			addButton(checkuser);
		}
		FlowLayoutContainer centerContainer=new FlowLayoutContainer();
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		luid=new Label();
		lname=new Label();
		luserType=new Label();
		leffective=new Label();
		lemail=new Label("");
		phoneContent=new HTMLPanel("");
		descriptionContent=new HTMLPanel("");
		p.add(new FieldLabel(luid, "用户账号"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(lname,"用户姓名"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(luserType,"用户状态"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(leffective,"用户类型"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(lemail, "邮箱地址"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(phoneContent, "手机号码"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(descriptionContent, "描述"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		
		centerContainer.add(p);
		setCenter(centerContainer);
	}

	@Override
	public void refresh(ZUser zu) {
		display(zu);
	}
	
	public void display(ZUser user){
		luid.setText(user.getUid());
		lname.setText(user.getName());
//		int effective = user.getEffective();
//        if (effective == 1) {
////        	effective.cellHtml = "<span class='icon-ok' style='margin-left:10px'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;审核通过</span>"
//        	leffective.setText("审核通过");
//        } else if(effective == -2) {
// //           e.cellHtml = "<span class='icon-no' style='margin-left:10px'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;审核拒绝</span>"
//        	leffective.setText("审核拒绝");
//        } else if(effective == 0) {
////            e.cellHtml = "<span class='icon-new' style='margin-left:10px'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;待审核</span>"
//        	leffective.setText("待审核");
//        } else if(effective == -1) {
////            e.cellHtml = "<span class='icon-cancel' style='margin-left:10px'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;已删除</span>"
//        	leffective.setText("已删除");
//        }
        leffective.setText(user.getIsEffective());
        luserType.setText(user.getUserType());
        lemail.setText(user.getEmail());
        phoneContent.getElement().setInnerHTML(UserUtil.getHtmlString(user.getPhone()));
        descriptionContent.getElement().setInnerHTML(UserUtil.getHtmlString(user.getDescription()));
	}
	
}