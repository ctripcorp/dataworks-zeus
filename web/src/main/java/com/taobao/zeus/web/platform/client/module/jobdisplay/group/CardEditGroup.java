package com.taobao.zeus.web.platform.client.module.jobdisplay.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sencha.gxt.cell.core.client.form.TextAreaInputCell;
import com.sencha.gxt.cell.core.client.form.TextAreaInputCell.Resizable;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.taobao.zeus.web.platform.client.module.jobdisplay.CenterTemplate;
import com.taobao.zeus.web.platform.client.module.jobdisplay.FormatUtil;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.FileUploadWidget;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.FileUploadWidget.UploadCallback;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.event.TreeNodeChangeEvent;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.Refreshable;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;


/**
 * group编辑功能
 * @author zhoufang
 *
 */
public class CardEditGroup extends CenterTemplate implements Refreshable<GroupModel>{
	private GroupModel model;
	private TextField name;
	private TextArea desc;
	private TextArea configs;
	private TextArea resources;

	private TextButton upload=new TextButton("上传资源文件", new SelectHandler() {
		@Override
		public void onSelect(SelectEvent event) {
			new FileUploadWidget("group", model.getId(),new UploadCallback() {
				@Override
				public void call(String name, String uri) {
					Map<String, String> map=new HashMap<String, String>();
					map.put("name", name);
					map.put("uri", uri);
					List<Map<String, String>> temp=new ArrayList<Map<String,String>>();
					temp.add(map);
					resources.setValue(resources.getValue()+"\n"+FormatUtil.convertResourcesToEditString(temp));
					
				}
			}).show();
		}
	});
	private TextButton save=new TextButton("保存", new SelectHandler() {
		@Override
		public void onSelect(SelectEvent event) {
			if(!resources.validate() || !configs.validate()){
				return;
			}
			model.setName(name.getValue());
			model.setDesc(desc.getValue());
			model.setLocalProperties(FormatUtil.parseProperties(configs.getValue()));
			model.setLocalResources(FormatUtil.parseResources(resources.getValue()));
			RPCS.getGroupService().updateGroup(model, new AbstractAsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					RPCS.getGroupService().getUpstreamGroup(model.getId(), new AbstractAsyncCallback<GroupModel>() {
						@Override
						public void onSuccess(GroupModel result) {
							presenter.getPlatformContext().getPlatformBus().fireEvent(new TreeNodeChangeEvent(result));
							presenter.display(result);
						}
					});
				}
			});
		}
	});
	
	private GroupPresenter presenter;
	public CardEditGroup(final GroupPresenter presenter){
		this.presenter=presenter;
		this.model=presenter.getGroupModel();
		name=new TextField();
		name.setAllowBlank(false);
		desc=new TextArea();
		
		FlowLayoutContainer centerContainer=new FlowLayoutContainer();
		centerContainer.setScrollMode(ScrollMode.AUTOY);
		
		FieldSet one=new FieldSet();
		one.setHeadingText("基本信息");
		VerticalLayoutContainer p1 = new VerticalLayoutContainer();
		p1.add(new FieldLabel(name, "名称"),new VerticalLayoutContainer.VerticalLayoutData(1, 1));
		p1.add(new FieldLabel(desc, "描述"),new VerticalLayoutContainer.VerticalLayoutData(1, 1));
		one.add(p1);
		
		centerContainer.add(one,new MarginData(5));
		
		final FieldSet two=new FieldSet();
		two.setHeadingText("配置项信息");
		configs=new TextArea();
		configs.setResizable(TextAreaInputCell.Resizable.BOTH);
		configs.addValidator(FormatUtil.propValidator);
		configs.setWidth(800);
		configs.setHeight(150);
		two.add(configs);
		
		centerContainer.add(two,new MarginData(5));
		
		final FieldSet three=new FieldSet();
		three.setHeadingText("资源信息");
		resources=new TextArea();
		resources.setResizable(Resizable.BOTH);
		resources.setWidth(800);
		resources.setHeight(150);
		resources.addValidator(FormatUtil.resourceValidator);
		three.add(resources);
		centerContainer.add(three,new MarginData(5));
		
		setCenter(centerContainer);
		
		addButton(new TextButton("返回", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				presenter.display(presenter.getGroupModel());
			}
		}));
		addButton(upload);
		addButton(save);
		
	}
	@Override
	public void refresh(GroupModel t) {
		this.model=t;
		name.setValue(t.getName());
		desc.setValue(t.getDesc());
		configs.setValue(FormatUtil.convertPropertiesToEditString(t.getLocalProperties()));
		resources.setValue(FormatUtil.convertResourcesToEditString(t.getLocalResources()));
	
	}
}
