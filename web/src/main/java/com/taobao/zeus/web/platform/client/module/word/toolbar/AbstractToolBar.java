package com.taobao.zeus.web.platform.client.module.word.toolbar;

import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.History;
import com.sencha.gxt.core.client.Style.LayoutRegion;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.taobao.zeus.web.platform.client.app.PlacePath;
import com.taobao.zeus.web.platform.client.app.PlacePath.App;
import com.taobao.zeus.web.platform.client.app.PlacePath.JobType;
import com.taobao.zeus.web.platform.client.app.PlatformPlace;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.DefaultPanel;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.FileUploadWidget;
import com.taobao.zeus.web.platform.client.module.jobdisplay.job.FileUploadWidget.UploadCallback;
import com.taobao.zeus.web.platform.client.module.jobmanager.WorkerGroupWindow;
import com.taobao.zeus.web.platform.client.module.profile.QuickHadoopConfig;
import com.taobao.zeus.web.platform.client.module.word.Word;
import com.taobao.zeus.web.platform.client.module.word.component.EditTab;
import com.taobao.zeus.web.platform.client.theme.ResourcesTool;
import com.taobao.zeus.web.platform.client.util.RPCS;
import com.taobao.zeus.web.platform.client.util.async.AbstractAsyncCallback;
import com.taobao.zeus.web.platform.client.util.async.PlatformAsyncCallback;

public abstract class AbstractToolBar extends ToolBar{
	
	protected TextButton run=new TextButton("运行",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			run(getEditTab().getNewContent());
		}
	});
	
	private void run(String content){
		String name=getFileModel().getName();
		if(name.indexOf(".")!=-1){
			String mode=null;
			String postfix=name.substring(name.lastIndexOf("."));
			if(".hive".equalsIgnoreCase(postfix)){
				mode="hive";
			}else if(".sh".equalsIgnoreCase(postfix)){
				mode="shell";
			}
			if(mode!=null){
				RPCS.getJobDebugService().debug(getFileModel().getId(),mode,
						content, getFileModel().getWorkerGroupId(), new AbstractAsyncCallback<String>() {
					@Override
					public void onSuccess(final String debugId) {
						getEditTab().getLogTabPanel().onStartDebug(debugId);
						getEditTab().show(LayoutRegion.SOUTH);
						getEditTab().expand(LayoutRegion.SOUTH);
					}
				});
			}
		}
	}
	protected TextButton selectRun=new TextButton("运行选中代码",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			run(getEditTab().getCodeMirror().getSelection());
		}
	});

	
	protected TextButton sync=new TextButton("同步任务",ResourcesTool.iconResources.icon_sync());
	protected TextButton syncJump=new TextButton("跳转调度任务",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			PlatformPlace pp=new PlacePath().toApp(App.Schedule).toJobType(JobType.MyJob).toDisplayJob(jumpJobId).create();
			History.newItem(pp.getToken());
		}
	});
	protected String jumpJobId;
	
	protected TextButton config=new TextButton("全局配置",new SelectHandler() {
		public void onSelect(SelectEvent event) {
			QuickHadoopConfig win=new QuickHadoopConfig();
			win.setModal(true);
			win.show();
		}
	});
	
	
	protected TextButton extend=new TextButton("扩展功能",ResourcesTool.iconResources.icon_extends());
	
	protected MenuItem lingoes=new MenuItem("生成脚本",new SelectionHandler<MenuItem>() {
		@Override
		public void onSelection(SelectionEvent<MenuItem> event) {

			new DefaultPanel(getFileModel().getId()).show();
		}
	});
	protected MenuItem upload=new MenuItem("上传资源",new SelectionHandler<MenuItem>() {
		@Override
		public void onSelection(SelectionEvent<MenuItem> event) {
			new FileUploadWidget("debug", getFileModel().getId(),
					new UploadCallback() {
						@Override
						public void call(final String name, final String uri) {
							StringBuffer sb = new StringBuffer();
							sb.append("download[").append(uri).append(' ').append(name)
								.append("]\n")
								.append(getCodeMirror().getValue());
							getCodeMirror().setValue(sb.toString());
						}
					}).show();
		}
	});
	protected MenuItem workgroup=new MenuItem("选择worker组",new SelectionHandler<MenuItem>() {
		@Override
		public void onSelection(SelectionEvent<MenuItem> event) {
			final WorkerGroupWindow chdwnd = new WorkerGroupWindow();
			chdwnd.setSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					if (chdwnd.getGrid().getSelectionModel()!=null) {
						String id = chdwnd.getGrid().getSelectionModel().getSelectedItem().getId();
						getFileModel().setWorkerGroupId(id);
						getEditTab().refreshWorkerGroupStatus(id);
						getEditTab().getContext().getFileSystem().updateWorkerGroupId(getFileModel().getId(), id, new PlatformAsyncCallback<Void>() {
							public void callback(Void t) {
							}
						});
					}
					chdwnd.hide();
				}
			});
			chdwnd.show();
		}
	});
	protected MouseUpHandler mouseUpHandler=new MouseUpHandler() {
		public void onMouseUp(MouseUpEvent event) {
			String selection=getCodeMirror().getSelection();
			if(selection!=null && !"".equals(selection.trim())){
				selectRun.enable();
			}else{
				selectRun.disable();
			}
		}
	};
	{
		run.setIcon(ResourcesTool.iconResources.icon_run());
		selectRun.setIcon(ResourcesTool.iconResources.icon_run());
		config.setIcon(ResourcesTool.iconResources.icon_config());
		upload.setIcon(ResourcesTool.iconResources.icon_upload());
		syncJump.setIcon(ResourcesTool.iconResources.icon_jump());
	}
	public abstract void onWordChange(Word word);

	protected abstract CodeMirror getCodeMirror();
	protected abstract FileModel getFileModel();
	protected abstract EditTab getEditTab();

}
