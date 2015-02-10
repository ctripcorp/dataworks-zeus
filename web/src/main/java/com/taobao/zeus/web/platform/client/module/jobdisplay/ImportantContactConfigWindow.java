package com.taobao.zeus.web.platform.client.module.jobdisplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.info.Info;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.util.Tuple;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.JobPresenter;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.ZUser;
import com.taobao.zeus.web.platform.client.util.ZUserContactTuple;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class ImportantContactConfigWindow extends Window{
	private JobPresenter jobPresenter;
	private TextButton add = new TextButton("添加",new SelectHandler() {
		private ComboBox<Map<String, String>> combo;
		private ListStore<Map<String, String>> store=new ListStore<Map<String, String>>(new ModelKeyProvider<Map<String, String>>() {
			public String getKey(Map<String, String> item) {
				return item.get("name");
			}
		});
		private TextButton submit = new TextButton("添加",new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				Map<String, String> selected = combo.getValue();
				if (combo.validate() && selected != null) {
					RPCS.getJobService().grantImportantContact(jobPresenter.getJobModel().getId(), selected.get("uid"), new AbstractAsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							window.hide();
							Info.display("成功", "授予重要联系人成功");
							ImportantContactConfigWindow.this.refresh();
						}
						
					});
				}
				
			}
		}
		);
		private Window window = new Window();
		{
			window.setHeadingText("添加重要联系人");
			window.setSize("350", "100");
			window.setModal(true);
			combo=new ComboBox<Map<String, String>>(store,new LabelProvider<Map<String, String>>() {
				public String getLabel(Map<String, String> item) {
					return item.get("name");
				}
			});
			combo.setForceSelection(true);
			combo.setTriggerAction(TriggerAction.QUERY);
			combo.setStore(store);
			window.add(new FieldLabel(combo, "选择关注者"));
			window.addButton(submit);
		}
		@Override
		public void onSelect(SelectEvent event) {
			store.clear();
			for(ZUser user : notImportantContactList){
				Map<String, String> md=new HashMap<String, String>();
				md.put("name", user.getName()+"("+user.getUid()+")");
				md.put("uid", user.getUid());
				store.add(md);
			}
			combo.reset();
			window.show();
		}
	}
	);
	private TextButton delete = new TextButton("撤销", new SelectHandler() {
		
		private ComboBox<Map<String, String>> combo;
		private ListStore<Map<String, String>> store=new ListStore<Map<String, String>>(new ModelKeyProvider<Map<String, String>>() {
			public String getKey(Map<String, String> item) {
				return item.get("name");
			}
		});
		private TextButton submit = new TextButton("撤销",new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				Map<String, String> selected = combo.getValue();
				if (combo.validate() && selected != null) {
					RPCS.getJobService().revokeImportantContact(jobPresenter.getJobModel().getId(), selected.get("uid"), new AbstractAsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							window.hide();
							Info.display("成功", "撤销重要联系人成功");
							ImportantContactConfigWindow.this.refresh();
						}
						
					});
				}
				
			}
		}
		);
		private Window window = new Window();
		{
			window.setHeadingText("撤销重要联系人");
			window.setSize("350", "100");
			window.setModal(true);
			combo=new ComboBox<Map<String, String>>(store,new LabelProvider<Map<String, String>>() {
				public String getLabel(Map<String, String> item) {
					return item.get("name");
				}
			});
			combo.setForceSelection(true);
			combo.setTriggerAction(TriggerAction.QUERY);
			combo.setStore(store);
			window.add(new FieldLabel(combo, "选择关注者"));
			window.addButton(submit);
		}
		@Override
		public void onSelect(SelectEvent event) {
			store.clear();
			for(ZUser user : importantContactList){
				Map<String, String> md=new HashMap<String, String>();
				md.put("name", user.getName()+"("+user.getUid()+")");
				md.put("uid", user.getUid());
				store.add(md);
			}
			combo.reset();
			window.show();
		}
	}
	);
	private HTMLPanel panel = new HTMLPanel("");
	private List<ZUser> importantContactList;
	private List<ZUser> notImportantContactList;
	
	public ImportantContactConfigWindow(JobPresenter jobPresenter){
		this();
		this.jobPresenter = jobPresenter;
		refresh();
	}
	
	public ImportantContactConfigWindow(){
		importantContactList = new ArrayList<ZUser>();
		notImportantContactList = new ArrayList<ZUser>();
		setHeadingText("管理重要联系人");
		setModal(true);
		setSize("300", "200");
		add(panel);
		addButton(add);
		addButton(delete);
		addHideHandler(new HideHandler() {
			public void onHide(HideEvent event) {
				if(jobPresenter!=null){
					RPCS.getJobService().getUpstreamJob(jobPresenter.getJobModel().getId(), new AbstractAsyncCallback<JobModel>() {
						@Override
						public void onSuccess(JobModel result) {
							jobPresenter.display(result);
						}
					});
				}
			}
		});
	}
	private void refresh(){
		notImportantContactList.clear();
		importantContactList.clear();
		RPCS.getJobService().getAllContactList(jobPresenter.getJobModel().getId(), new AbstractAsyncCallback<List<ZUserContactTuple>>() {

			@Override
			public void onSuccess(List<ZUserContactTuple> result) {
				for(ZUserContactTuple tuple : result){
					if (tuple.getIsImportant()) {
						importantContactList.add(tuple.getZuser());
					}else {
						notImportantContactList.add(tuple.getZuser());
					}
				}
				StringBuffer buffer=new StringBuffer();
				for(ZUser user:importantContactList){
					buffer.append("<span title='"+user.getUid()+"'>"+user.getName()+",</span>");
				}
				panel.getElement().setInnerHTML(buffer.toString());
			}

		});
		
	}
}
