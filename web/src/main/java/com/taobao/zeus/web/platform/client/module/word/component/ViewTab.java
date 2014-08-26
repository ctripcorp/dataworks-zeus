package com.taobao.zeus.web.platform.client.module.word.component;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirrors;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;

public class ViewTab extends SimpleContainer{

	private FileModel model;
	private ContentPanel codePanel = new ContentPanel();
	private CodeMirror codeMirror;
	public ViewTab(FileModel model){
		this.model=model;
		this.codeMirror = getCodeMirror();
		codePanel.setHeaderVisible(false);
		codePanel.add(getCodeMirror());
		
		add(codePanel);
		//refresh(model.getContent());
	}
	
	public void refresh(String content){
		String mode="";
		if(model.getName().endsWith(".hive")){
			mode="mysql";
		}else if(model.getName().endsWith(".sh")){
			mode="shell";
		}else if(model.getName().endsWith(".html")||model.getName().endsWith(".htm")){
			mode="htmlmixed";
		}else{
			mode="htmlmixed";
		}
		CodeMirrors.runMode(content, mode, getElement());
	}
	
	public CodeMirror getCodeMirror() {
		if (codeMirror == null) {
			CodeMirrorConfig editCMC = new CodeMirrorConfig();
			editCMC.value = model.getContent();
			if(model.getName().endsWith(".hive")){
				editCMC.mode="mysql";
			}else if(model.getName().endsWith(".sh")){
				editCMC.mode="shell";
			}else if(model.getName().endsWith(".html")||model.getName().endsWith(".htm")){
				editCMC.mode="htmlmixed";
			}else{
				editCMC.mode="htmlmixed";
			}
			editCMC.readOnly = true;
			codeMirror = new CodeMirror(editCMC);
		}
		return codeMirror;
	}
}
