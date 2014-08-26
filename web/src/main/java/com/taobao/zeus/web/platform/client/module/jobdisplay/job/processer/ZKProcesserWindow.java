package com.taobao.zeus.web.platform.client.module.jobdisplay.job.processer;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.ProcesserType;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.ProcesserType.ZooKeeperP;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;

public class ZKProcesserWindow extends Window{
	private ZooKeeperP zooKeeperP=new ZooKeeperP();
	
	private ToggleGroup zkType;
	private Radio useDefault;
	private Radio useCustom;
	private FieldLabel hostFieldWapper;
	private FieldLabel pathFieldWapper;
	private TextField hostField;
	private TextField pathField;
	
	private FormPanel formPanel;
	
	private JobModel jobModel;
	
	private TextButton add=new TextButton("确定", new SelectHandler() {
		@Override
		public void onSelect(SelectEvent event) {
			if(formPanel.isValid()){
				if(isOn()) {
					zkBtn.setText("自定义ZK[开启]");
				}else{
					zkBtn.setText("自定义ZK[关闭]");
				}
				ZKProcesserWindow.this.hide();
			}
		}
	});
	private TextButton zkBtn;
	public boolean isOn() {
		return useCustom.getValue();
	}
	public void setOff() {
		useCustom.setValue(false, true);
		useDefault.setValue(true, true);
		pathField.setValue("");
		hostField.setValue("");
		add.fireEvent(new SelectEvent());
	}
	public ZKProcesserWindow(TextButton zk){
		setModal(true);
		this.zkBtn = zk;
		setHeadingText("ZooKeeper通知 配置");
		setSize("400", "200");
		
		formPanel=new FormPanel();
		
		VerticalLayoutContainer p = new VerticalLayoutContainer();
		formPanel.add(p);
		
		zkType=new ToggleGroup();
		useDefault=new Radio();
		useDefault.setBoxLabel("关闭");
		useCustom=new Radio();
		useCustom.setBoxLabel("开启");
		zkType.add(useDefault);
		zkType.add(useCustom);
		HorizontalPanel hp = new HorizontalPanel();
	    hp.add(useDefault);
	    hp.add(useCustom);
		
	    useDefault.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
					hostFieldWapper.hide();
					pathFieldWapper.hide();
					hostField.setAllowBlank(true);
					pathField.setAllowBlank(true);
					formPanel.isValid();
				}
			}
		});
	    useCustom.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
//					AlertMessageBox box=new AlertMessageBox("警告", "强烈建议使用系统默认ZK，定制ZK只是为了适应老的Job");
//					box.show();
					if(hostField.getValue()==null||hostField.getValue().isEmpty()) {
						hostField.setValue("${instance.processer.zk.host}");
					}
					hostField.setToolTip("请在配置项中配合填写 instance.processer.zk.host");
					if(pathField.getValue()==null||pathField.getValue().isEmpty()) {
						pathField.setValue("${instance.processer.zk.path}");
					}
					pathField.setToolTip("请在配置项中配合填写 instance.processer.zk.path");
					hostFieldWapper.show();
					pathFieldWapper.show();
					hostField.setAllowBlank(false);
					pathField.setAllowBlank(false);
				}
			}
		});
		
		hostField=new TextField();
		hostField.setWidth(200);
		
		pathField=new TextField();
		pathField.setWidth(200);
		pathField.addValidator(new Validator<String>() {
			@Override
			public List<EditorError> validate(Editor<String> editor, String value) {
				List<EditorError> list=new ArrayList<EditorError>();
				if(value!=null && value.startsWith("/zeus/")){
					list.add(new DefaultEditorError(editor, "不得将通知设定到其他Job任务节点上", value));
				}
				return list;
			}
		});
		hostFieldWapper=new FieldLabel(hostField,"ZK服务器");
		pathFieldWapper=new FieldLabel(pathField,"路径");
		p.add(new FieldLabel(hp,"ZK类型"));
		p.add(hostFieldWapper);
		p.add(pathFieldWapper);
		
		
		add(formPanel,new MarginData(5));
		
		addButton(add);
		addButton(new TextButton("取消",new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				ZKProcesserWindow.this.hide();
			}
		}));
	}

	public ProcesserType getProcesserType() {
		zooKeeperP.setUseDefault(zkType.getValue()==useDefault?true:false);
		zooKeeperP.setHost(hostField.getValue());
		zooKeeperP.setPath(pathField.getValue());
		return zooKeeperP;
	}
	
	public void setProcesser(ZooKeeperP p){
		this.zooKeeperP = p;
		this.useCustom.setValue(true,true);
		hostField.setAllowBlank(false);
		pathField.setAllowBlank(false);
		this.pathField.setValue(p.getPath());
		this.hostField.setValue(p.getHost());
		hostFieldWapper.show();
		pathFieldWapper.show();
		add.fireEvent(new SelectEvent());
	}

}
