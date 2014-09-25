


































package com.taobao.zeus.web.platform.client.module.jobdisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.info.Info;
import com.taobao.zeus.web.platform.client.module.jobdisplay.group.GroupPresenter;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobPresenter;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class ChooseConfigWindow extends Window{

	private JobPresenter jobPresenter;
	private ArrayList<String> Startmsg = new ArrayList<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
	    add("手动执行任务开始");
	    add("手动恢复任务开始");
	}};
	private int type;
	private ArrayList<String> Errormsg = new ArrayList<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
	    add("手动执行任务失败");
	    add("手动恢复任务失败");
	}};	
		private ListStore<Map<String, String>> store=new ListStore<Map<String, String>>(new ModelKeyProvider<Map<String, String>>() {
			public String getKey(Map<String, String> item) {
				return item.get("name")+"("+item.get("uid")+")";
			}
		});
		private ComboBox<Map<String, String>> combo;
		private TextButton submit=new TextButton("执行", new SelectHandler() {
			public void onSelect(SelectEvent event) {
				Map<String, String> md=combo.getValue();
				if(combo.validate() && md!=null){
					if(jobPresenter!=null){
						final AutoProgressMessageBox wait=new AutoProgressMessageBox("运行中","doing");
						wait.auto();
						wait.show();
						String actionid = md.get("actionid");System.out.println(actionid);System.out.println(type);
						RPCS.getJobService().run(actionid, type, new AbstractAsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								wait.hide();
								Info.display("成功", Startmsg.get(type-1));
								hide();
							}
							@Override
							public void onFailure(Throwable caught) {
								wait.hide();
								AlertMessageBox alert=new AlertMessageBox("失败", Errormsg.get(type-1));
								alert.show();
							};
						});
					}
				}
			}
		});
		private Window win=new Window();
		{
			win.setHeadingText("选择JOB版本");
			win.setSize("350", "100");
			win.setModal(true);
			combo=new ComboBox<Map<String, String>>(store,new LabelProvider<Map<String, String>>() {
				public String getLabel(Map<String, String> item) {
					return item.get("name");
				}
			});
			combo.setForceSelection(true);
			combo.setTriggerAction(TriggerAction.QUERY);
			combo.setStore(store);
			win.add(new FieldLabel(combo, "JOB"));
			win.addButton(submit);
		}

	public ChooseConfigWindow(JobPresenter presenter){
		this();
		this.jobPresenter=presenter;
		refresh();
	}
	public ChooseConfigWindow(JobPresenter presenter,int type){
		this();
		this.type = type;
		this.jobPresenter=presenter;
		refresh();
	}
	public ChooseConfigWindow() {
		setHeadingText("选择JOB版本");
		setSize("350", "100");
		setModal(true);
		combo=new ComboBox<Map<String, String>>(store,new LabelProvider<Map<String, String>>() {
			public String getLabel(Map<String, String> item) {
				return item.get("name");
			}
		});
		combo.setForceSelection(true);
		combo.setTriggerAction(TriggerAction.QUERY);
		combo.setStore(store);
		add(new FieldLabel(combo, "选择JOB版本"));
		addButton(submit);
		
		// TODO Auto-generated constructor stub
	}
	private void refresh(){
		if(jobPresenter!=null){
			RPCS.getJobService().getJobACtion(jobPresenter.getJobModel().getId(), new AbstractAsyncCallback<List<Long>>() {
				public void onSuccess(List<Long> result) {
					if(result.size()==0){
						AlertMessageBox alert=new AlertMessageBox("错误", "没有其他版本");
						alert.show();
					}else{
						Iterator<Long> itr = result.iterator();
						while (itr.hasNext()) {
						    Object nextObj = itr.next();
						    Map<String, String> md=new HashMap<String, String>();
						    Long actionid = (Long) nextObj;
						    String str = Long.toString(actionid);
							md.put("name",str);
							md.put("actionid", str);
							store.add(md);
						}
						combo.reset();
						show();
					}
				}
			});
		}
		
	}

}