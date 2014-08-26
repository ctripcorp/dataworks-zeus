package com.taobao.zeus.web.platform.client.module.jobdisplay.group;

import java.util.Map;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.taobao.zeus.web.platform.client.module.jobdisplay.AdminConfigWindow;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.TreeKeyProviderTool;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeChangeEvent;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.Refreshable;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class CardInfo extends CenterTemplate implements Refreshable<GroupModel>{

	private TextButton overall=new TextButton("任务总览",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			presenter.displayOverall();
		}
	});
	
	private TextButton running=new TextButton("自动任务",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			presenter.displayRunning();
		}
	});
	private TextButton manual=new TextButton("手动任务",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			presenter.displayManual();
		}
	});
	
	private TextButton addGroup=new TextButton("添加组",new SelectHandler() {
		@Override
		public void onSelect(SelectEvent event) {
			new NewGroupWindow(presenter).show();
		}
	});
	private TextButton addJob=new TextButton("添加任务",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			new NewJobWindow(presenter.getPlatformContext().getPlatformBus(),presenter.getGroupModel()).show();
		}
	});
	private TextButton editGroup=new TextButton("编辑",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			presenter.displayEditGroup();
		}
	});
	private TextButton deleteGroup=new TextButton("删除",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			ConfirmMessageBox box=new ConfirmMessageBox("删除组", "你确认删除此组?");
			box.addHideHandler(new HideHandler() {
				public void onHide(HideEvent event) {
					Dialog btn = (Dialog) event.getSource();
					if(btn.getHideButton().getText().equalsIgnoreCase("yes")){
						RPCS.getGroupService().deleteGroup(presenter.getGroupModel().getId(), new AbstractAsyncCallback<Void>() {
							public void onSuccess(Void result) {
								TreeNodeChangeEvent event=new TreeNodeChangeEvent();
								event.setNeedSelectProviderKey(TreeKeyProviderTool.genGroupProviderKey(presenter.getGroupModel().getParent()));
								presenter.getPlatformContext().getPlatformBus().fireEvent(event);
							}
						});
					}
				}
			});
			box.show();
		}
	});
	private TextButton configAdmin=new TextButton("配置管理员",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			new AdminConfigWindow(presenter).show();
		}
	});
	
	private FieldSet baseFieldSet;
	private FieldSet configFieldSet;
	private FieldSet configParentField;
	private FieldSet resourceField;
	private FieldSet resourceParentField;
	
	private Label baseId;
	private Label baseName;
	private Label baseOwner;
	private Label baseDesc;
	private Label baseFollers;
	private Label baseAdmins;
	
	private HTMLPanel configContent;
	private HTMLPanel configParentContent;
	private HTMLPanel resourceContent;
	private HTMLPanel resourceParentContent;
	
	private GroupPresenter presenter;
	
	private static final String TIPS_GROUP="schedule-cardinfo-group";
	
	public CardInfo(final GroupPresenter presenter){
		this.presenter=presenter;
		addButton(overall);
		addButton(running);
		addButton(manual);
		addButton(addGroup);
		addButton(addJob);
		addButton(editGroup);
		addButton(deleteGroup);
		addButton(configAdmin);
		
		
		FlowLayoutContainer centerContainer=new FlowLayoutContainer();
		centerContainer.add(getBaseFieldSet(),new MarginData(3));
		centerContainer.add(getConfigFieldSet(),new MarginData(3));
		centerContainer.add(getResourceField(),new MarginData(3));
		centerContainer.add(getConfigParentField(),new MarginData(3));
		centerContainer.add(getResourceParentField(),new MarginData(3));
		setCenter(centerContainer);
		
		
	}
	
	public void display(GroupModel model){
		baseId.setText(model.getId());
		baseName.setText(model.getName());
		baseOwner.setText(model.getOwnerName());
		baseDesc.setText(model.getDesc());
		baseFollers.setText(model.getFollows().toString());
		baseAdmins.setText(model.getAdmins().toString());
		
		Map<String, String> config=model.getLocalProperties();
		StringBuffer sb=new StringBuffer("<div style='font-size:13px'>");
		for(String key:config.keySet()){
			if(key.startsWith("secret.")){
				sb.append(key+"=*<br/>");
			}else if(key.equals("hadoop.hadoop.job.ugi")){
				String value=config.get(key);
				if(value.indexOf("#")!=-1){
					value=value.substring(0, value.indexOf("#"))+"*";
				}
				sb.append(key+"="+value+"<br/>");
			}else{
				sb.append(key+"="+config.get(key)+"<br/>");
			}
		}
		sb.append("</div>");
		configContent.getElement().setInnerHTML(sb.toString());
		
		Map<String, String> configAll=model.getAllProperties();
		sb=new StringBuffer("<div style='font-size:13px'>");
		for(String key:configAll.keySet()){
			if(config.get(key)==null){
				sb.append(key+"="+configAll.get(key)+"<br/>");
			}
		}
		sb.append("</div>");
		configParentContent.getElement().setInnerHTML(sb.toString());
		
		
		sb=new StringBuffer("<div style='font-size:13px'>");
		for(Map<String, String> record:model.getLocalResources()){
			sb.append("<p title='"+record.get("uri")+"'>"+record.get("name")+"</p>");
		}
		sb.append("</div>");
		resourceContent.getElement().setInnerHTML(sb.toString());
		
		sb=new StringBuffer("<div style='font-size:13px'>");
		for(Map<String, String> record:model.getAllResources()){
			if(!model.getLocalResources().contains(record)){
				sb.append("<p title='"+record.get("uri")+"'>"+record.get("name")+"</p>");
			}
		}
		sb.append("</div>");
		resourceParentContent.getElement().setInnerHTML(sb.toString());
		
		checkPermissionDisplay(model);
	}
	
	private void checkPermissionDisplay(GroupModel model){
		if(model.isAdmin()){
			if(model.isDirectory()){
				addGroup.enable();
				addJob.disable();
			}else{
				addGroup.disable();
				addJob.enable();
			}
			editGroup.enable();
			if(presenter.getPlatformContext().getUser().getUid().equals(model.getOwner())){
				deleteGroup.enable();
			}else{
				deleteGroup.disable();
			}
		}else{
			addGroup.disable();
			deleteGroup.disable();
			addJob.disable();
			editGroup.disable();
		}
		if(model.getOwners().contains(presenter.getPlatformContext().getUser().getUid())){
			configAdmin.enable();
		}else{
			configAdmin.disable();
		}
	}

	public FieldSet getBaseFieldSet() {
		if(baseFieldSet==null){
			baseFieldSet=new FieldSet();
			baseFieldSet.setHeadingText("基本信息");
			
			VerticalLayoutContainer p = new VerticalLayoutContainer();
			
			baseId=new Label();
			baseName=new Label();
			baseOwner=new Label();
			baseDesc=new Label();
			baseFollers=new Label();
			baseAdmins=new Label();
			
			p.add(new FieldLabel(baseId, "id"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
			p.add(new FieldLabel(baseName,"名称"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
			p.add(new FieldLabel(baseOwner,"所有人"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
			p.add(new FieldLabel(baseDesc,"描述"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
			p.add(new FieldLabel(baseFollers,"关注人员"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
			p.add(new FieldLabel(baseAdmins,"管理员"),new VerticalLayoutContainer.VerticalLayoutData(1, -1));
			baseFieldSet.add(p);
		}
		return baseFieldSet;
	}

	public FieldSet getConfigParentField() {
		if(configParentField==null){
			configParentField=new FieldSet();
			configParentField.setHeadingText("继承的配置项信息");
			configParentField.setCollapsible(true);
			
			configParentContent=new HTMLPanel("");
			configParentField.add(configParentContent);
		}
		return configParentField;
	}

	public FieldSet getResourceField() {
		if(resourceField==null){
			resourceField=new FieldSet();
			resourceField.setHeadingText("资源信息");
			resourceContent=new HTMLPanel("");
			resourceField.add(resourceContent);
		}
		return resourceField;
	}

	public FieldSet getResourceParentField() {
		if(resourceParentField==null){
			resourceParentField=new FieldSet();
			resourceParentField.setHeadingText("继承的资源信息");
			resourceParentContent=new HTMLPanel("");
			resourceParentField.add(resourceParentContent);
		}
		return resourceParentField;
	}

	public FieldSet getConfigFieldSet() {
		if(configFieldSet==null){
			configFieldSet=new FieldSet();
			configFieldSet.setHeadingText("配置项信息");
			
			configContent=new HTMLPanel("");
			configFieldSet.add(configContent);
		}
		return configFieldSet;
	}
	@Override
	public void refresh(GroupModel t) {
		display(t);
	}
}
