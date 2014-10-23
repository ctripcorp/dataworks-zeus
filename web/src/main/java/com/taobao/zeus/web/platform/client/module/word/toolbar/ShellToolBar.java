package com.taobao.zeus.web.platform.client.module.word.toolbar;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.OnChangeListener;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.CheckableJobTree;
import com.taobao.zeus.web.platform.client.module.jobmanager.GroupJobTreeModel;
import com.taobao.zeus.web.platform.client.module.jobmanager.JobModel;
import com.taobao.zeus.web.platform.client.module.word.DataxWidget;
import com.taobao.zeus.web.platform.client.module.word.DataxWidget.Callback;
import com.taobao.zeus.web.platform.client.module.word.ShellWord;
import com.taobao.zeus.web.platform.client.module.word.Word;
import com.taobao.zeus.web.platform.client.module.word.component.EditTab;
import com.taobao.zeus.web.platform.client.theme.ResourcesTool;
import com.taobao.zeus.web.platform.client.util.GWTEnvironment;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.ToolUtil;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;

public class ShellToolBar extends AbstractToolBar{

	private ShellWord shellWord;
	
	public ShellToolBar(){
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
		if(shellWord!=null){
			getCodeMirror().removeChangeListener(changeListener);
			getCodeMirror().removeMouseUpHandler(mouseUpHandler);
		}
		shellWord=(ShellWord) word;
		getCodeMirror().addChangeListener(changeListener);
		getCodeMirror().addMouseUpHandler(mouseUpHandler);
		changeListener.onChange();
	}
	
	private OnChangeListener changeListener=new OnChangeListener() {
		public void onChange() {
			String jobId=ToolUtil.extractSyncToId(shellWord.getEditTab().getNewContent());
			if(jobId!=null){
				jumpJobId=jobId;
				syncJump.show();
			}else{
				syncJump.hide();
			}
			ShellToolBar.this.doLayout();
		}
	};
	
	
	private SelectHandler syncHandler=new SelectHandler() {
		public void onSelect(SelectEvent event) {
			// #sync[123->456]
			String[] lines=shellWord.getEditTab().getNewContent().split("\n");
			boolean exist=false;
			for(String line:lines){
				if(line.startsWith("#sync[")){
					final String jobId=line.substring(line.indexOf("->")+2, line.indexOf("]"));
					syncScript(jobId,false);
					exist=true;
					break;
				}
			}
			if(!exist){
				final CheckableJobTree tree=new CheckableJobTree();
				tree.getTree().setCheckable(false);
				tree.getTree().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				tree.setSelectHandler(new SelectHandler() {
					public void onSelect(SelectEvent event) {
						GroupJobTreeModel job=tree.getTree().getSelectionModel().getSelectedItem();
						if(job!=null){
							if(job.isGroup()){
								new AlertMessageBox("错误","不能选择组").show();
							}else{
								syncScript(job.getId(),true);
							}
						}
					}
				});
				tree.show();
			}
		}
		private void syncScript(final String jobId,final boolean firstTime){
			RPCS.getJobService().getUpstreamJob(jobId, new AbstractAsyncCallback<JobModel>() {
				public void onSuccess(JobModel result) {
					if(!result.getJobRunType().equals(JobModel.SHELL)){
						new AlertMessageBox("错误","目标任务不是shell程序，无法同步").show();
						return;
					}
					StringBuffer sb=new StringBuffer("您要同步的目标任务信息如下：<br/><br/>");
					sb.append("id:"+result.getId()+"<br/>");
					sb.append("名称:"+result.getName()+"<br/>");
					sb.append("所有人:"+result.getOwnerName()+"("+result.getOwner()+")<br/>");
					sb.append("自动调度:"+(result.getAuto()?"开启":"关闭")+"<br/>");
					sb.append("您确认要进行同步吗?");
					ConfirmMessageBox confirm=new ConfirmMessageBox("同步脚本", sb.toString());
					confirm.addHideHandler(new HideHandler() {
						public void onHide(HideEvent event) {
							Dialog dialog=(Dialog)event.getSource();
							if(dialog.getHideButton().getText().equals(DefaultMessages.getMessages().messageBox_yes())){
								if(firstTime){
									shellWord.getEditTab().getCodeMirror().setValue("#sync["+shellWord.getFileModel().getId()+"->"
											+jobId+"]\n"+shellWord.getEditTab().getNewContent());
								}
								RPCS.getJobService().syncScript(jobId, shellWord.getEditTab().getNewContent(), new AbstractAsyncCallback<Void>() {
									public void onSuccess(Void result) {
										Info.display("同步成功", "同步成功");
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
	protected CodeMirror getCodeMirror() {
		return shellWord.getEditTab().getCodeMirror();
	}

	@Override
	protected FileModel getFileModel() {
		return shellWord.getFileModel();
	}

	@Override
	protected EditTab getEditTab() {
		return shellWord.getEditTab();
	}
	
}
