package com.taobao.zeus.web.platform.client.module.word.toolbar;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.OnChangeListener;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.CheckableJobTree;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupJobTreeModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.module.word.HiveWord;
import com.taobao.zeus.web.platform.client.module.word.Word;
import com.taobao.zeus.web.platform.client.module.word.component.EditTab;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.ToolUtil;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class HiveToolBar extends AbstractToolBar{

	private HiveWord hiveWord;
	
	public HiveToolBar(){
		sync.addSelectHandler(syncHandler);
		add(run);
		add(selectRun);
		selectRun.disable();
		add(new SeparatorToolItem());
		add(sync);
		add(syncJump);
		add(new SeparatorToolItem());
		Menu menu=new Menu();
		menu.add(upload);
		menu.add(lingoes);
		extend.setMenu(menu);
		add(extend);
		add(new SeparatorToolItem());
		add(config);
	}
	
	@Override
	public void onWordChange(Word word) {
		if(hiveWord!=null){
			hiveWord.getEditTab().getCodeMirror().removeChangeListener(changeListener);
			hiveWord.getEditTab().getCodeMirror().removeMouseUpHandler(mouseUpHandler);
		}
		hiveWord=(HiveWord) word;
		hiveWord.getEditTab().getCodeMirror().addChangeListener(changeListener);
		hiveWord.getEditTab().getCodeMirror().addMouseUpHandler(mouseUpHandler);
		changeListener.onChange();
	}

	@Override
	protected CodeMirror getCodeMirror() {
		return hiveWord.getEditTab().getCodeMirror();
	}

	@Override
	protected FileModel getFileModel() {
		return hiveWord.getFileModel();
	}
	
	private OnChangeListener changeListener=new OnChangeListener() {
		public void onChange() {
			String jobId=ToolUtil.extractSyncToId(hiveWord.getEditTab().getNewContent());
			if(jobId!=null){
				jumpJobId=jobId;
				syncJump.show();
			}else{
				syncJump.hide();
			}
			HiveToolBar.this.doLayout();
		}
	};
	
	private SelectHandler syncHandler=new SelectHandler() {
		public void onSelect(SelectEvent event) {
			// --sync[123->456]
			String[] lines = hiveWord.getEditTab().getNewContent()
					.split("\n");
			boolean exist = false;
			for (String line : lines) {
				if (line.startsWith("--sync[")) {
					final String jobId = line.substring(
							line.indexOf("->") + 2,
							line.indexOf("]"));
					syncScript(jobId,false);
					exist = true;
					break;
				}
			}
			if (!exist) {
				final CheckableJobTree tree = new CheckableJobTree();
				tree.getTree().setCheckable(false);
				tree.getTree().getSelectionModel()
						.setSelectionMode(SelectionMode.SINGLE);
				tree.setSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						GroupJobTreeModel job = tree.getTree()
								.getSelectionModel()
								.getSelectedItem();
						if (job != null) {
							if (job.isGroup()) {
								new AlertMessageBox("错误","不能选择组").show();
							} else {
								syncScript(job.getId(),true);
							}
						}
					}
				});
				tree.show();
			}
		}
		private void syncScript(final String jobId,final boolean firstTime) {
			RPCS.getJobService().getUpstreamJob(jobId,
					new AbstractAsyncCallback<JobModel>() {
						@Override
						public void onSuccess(JobModel result) {
							if (!result.getJobRunType().equals(JobModel.HIVE)) {
								new AlertMessageBox("错误","目标任务不是Hive程序，无法同步").show();
								return;
							}
							StringBuffer sb = new StringBuffer("您要同步的目标任务信息如下：<br/><br/>");
							sb.append("id:" + result.getId()+ "<br/>");
							sb.append("名称:" + result.getName()+ "<br/>");
							sb.append("所有人:"+ result.getOwnerName()+ "(" + result.getOwner()+ ")<br/>");
							sb.append("自动调度:"+ (result.getAuto() ? "开启": "关闭") + "<br/>");
							sb.append("您确认要进行同步吗?");
							ConfirmMessageBox confirm = new ConfirmMessageBox("同步脚本", sb.toString());
							confirm.addHideHandler(new HideHandler() {
								@Override
								public void onHide(HideEvent event) {
									Dialog dialog = (Dialog) event.getSource();
									if (dialog.getHideButton().getText().equals(DefaultMessages.getMessages().messageBox_yes())) {
										if(firstTime){
											hiveWord.getEditTab().getCodeMirror().setValue(
													"--sync["+ getFileModel().getId()+ "->"+ jobId+ "]\n"+ hiveWord.getEditTab()
													.getNewContent());
										}
										RPCS.getJobService().syncScript(jobId,hiveWord.getEditTab().getNewContent(),
											new AbstractAsyncCallback<Void>() {
												@Override
												public void onSuccess(
														Void result) {
													Info.display("同步成功","同步成功");
												}
											});
									}
								}
							});
							confirm.show();
						}
					});
		}
	};

	@Override
	protected EditTab getEditTab() {
		return hiveWord.getEditTab();
	}
}
