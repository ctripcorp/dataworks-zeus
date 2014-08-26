package com.taobao.zeus.web.platform.client.module.jobdisplay.group;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.module.jobmanager.TreeKeyProviderTool;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeChangeEvent;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class CardAddGroup extends CenterTemplate{
	private FormPanel panel;
	private TextField name;
	private ToggleGroup group;
	private Radio leaf;
	private Radio dir;
	
	private GroupPresenter presenter;
	
	public CardAddGroup(GroupPresenter presenter) {
		this.presenter=presenter;
		panel = new FormPanel();
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		panel.add(p,new MarginData(5));
		name = new TextField();
		name.setToolTipConfig(new ToolTipConfig("组名称", "必填，可修改"){{setDismissDelay(0);}});

		dir = new Radio();
		dir.setBoxLabel("目录组");
		dir.setToolTipConfig(new ToolTipConfig("目录组，不可修改", "该类型的组下面只能添加叶子组，不能添加Job任务"){{setDismissDelay(0);}});
		leaf = new Radio();
		leaf.setBoxLabel("叶子组");
		leaf.setToolTipConfig(new ToolTipConfig("叶子组，不可修改", "该类型的组下只能添加Job任务，不能添加组"){{setDismissDelay(0);}});
		HorizontalPanel radios=new HorizontalPanel();
		radios.add(dir);
		radios.add(leaf);
		group=new ToggleGroup();
		group.add(dir);
		group.add(leaf);


		p.add(new FieldLabel(name, "组名称(*)"), new VerticalLayoutData(-18, -1));
		p.add(new FieldLabel(radios,"组类型(*)"), new VerticalLayoutData(-18, -1));
		
		setCenter(panel);

		addButton(new TextButton("返回", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				CardAddGroup.this.presenter.display(CardAddGroup.this.presenter.getGroupModel());
			}
		}));
		addButton(new TextButton("确定", new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				if (name.getValue()==null || name.getValue().trim().equals("")
						|| group.getValue() == null) {
					AlertMessageBox alert=new AlertMessageBox("警告", "必填项不能为空");
					alert.show();
				}else{
					submit();
				}
			}
		}));
		
	}
	
	
	private void submit(){
		String gname=name.getValue();
		Boolean isDirectory=group.getValue()==dir;
		RPCS.getGroupService().createGroup(gname, presenter.getGroupModel().getId(), isDirectory, new AbstractAsyncCallback<String>() {
			@Override
			public void onSuccess(String groupId) {
				presenter.display(presenter.getGroupModel());
				Info.display("操作成功", "新建组成功");
				TreeNodeChangeEvent event=new TreeNodeChangeEvent();
				event.setNeedSelectProviderKey(TreeKeyProviderTool.genGroupProviderKey(groupId));
				presenter.getPlatformContext().getPlatformBus().fireEvent(event);
			}
		});
	}
}