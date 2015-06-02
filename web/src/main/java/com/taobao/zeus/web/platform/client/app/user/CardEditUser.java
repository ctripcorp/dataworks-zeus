package com.taobao.zeus.web.platform.client.app.user;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.ZUser;

public class CardEditUser extends Window {

	private VerticalLayoutContainer container;
	
	private TextField name_text;
	private TextField lemail;
	private TextArea phone_text;
	private TextArea description_text;
	
	private Label leffective;
	private Label luid;
	private Label luserType; 
	
	private AsyncCallback<ZUser> callback;
	
	
	public CardEditUser(final UserPresenter presenter,final ZUser user) {
		setHeadingText("编辑用户信息");
		setModal(true);
		setHeight(560);
		setWidth(420);
		add(getLayoutContainer());
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		p.setWidth(400);
		luid=new Label();
		name_text=new TextField();
		name_text.setWidth(150);
		name_text.addValidator(new EmptyValidator<String>());
		name_text.setAutoValidate(true);
		luserType=new Label();
		leffective=new Label();
		
		lemail = new TextField();
		lemail.setWidth(350);
		lemail.addValidator(new EmptyValidator<String>());
		lemail.setAutoValidate(true);
		
		phone_text = new TextArea();
		phone_text.setWidth(350);
		phone_text.setHeight(148);
		phone_text.addValidator(new EmptyValidator<String>());
		phone_text.setAutoValidate(true);
		
		description_text = new TextArea();
		description_text.setWidth(350);
		description_text.setHeight(148);
		description_text.addValidator(new EmptyValidator<String>());
		description_text.setAutoValidate(true);
		
		p.add(new FieldLabel(luid, "用户账号"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(name_text,"用户姓名"),new VerticalLayoutContainer.VerticalLayoutData(1, -1, new Margins(10)));
		p.add(new FieldLabel(luserType,"用户状态"),new VerticalLayoutContainer.VerticalLayoutData(1, -1, new Margins(10)));
		p.add(new FieldLabel(leffective,"用户类型"),new VerticalLayoutContainer.VerticalLayoutData(1, -1, new Margins(10)));
		p.add(new FieldLabel(lemail, "邮箱地址"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(phone_text,"手机号码"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		p.add(new FieldLabel(description_text,"描述"),new VerticalLayoutContainer.VerticalLayoutData(1, -1,new Margins(10)));
		
		getLayoutContainer().add(p);
		addButton(new TextButton("保存",new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				user.setDescription(description_text.getValue());
				user.setEmail(lemail.getValue());
				user.setName(name_text.getValue());
				user.setPhone(phone_text.getValue());
				RPCS.getUserService().updateUser(user, callback);
				CardEditUser.this.hide();
			}
		}));
		refresh(user);
	}
	
	public void refresh(ZUser user){
		luid.setText(user.getUid());
		name_text.setValue(user.getName());
        leffective.setText(user.getIsEffective());
        luserType.setText(user.getUserType());
        phone_text.setValue(user.getPhone());
        lemail.setValue(user.getEmail());
        description_text.setValue(user.getDescription());
	}
	
	public VerticalLayoutContainer getLayoutContainer(){
		if(container==null){
			container=new VerticalLayoutContainer();
		}
		return container;
	}

	public AsyncCallback<ZUser> getCallback() {
		return callback;
	}

	public void setCallback(AsyncCallback<ZUser> callback) {
		this.callback = callback;
	}

}
