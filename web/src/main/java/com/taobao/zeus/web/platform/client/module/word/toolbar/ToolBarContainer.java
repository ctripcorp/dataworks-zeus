package com.taobao.zeus.web.platform.client.module.word.toolbar;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror;
import com.taobao.zeus.web.platform.client.module.filemanager.FileModel;
import com.taobao.zeus.web.platform.client.module.word.HiveWord;
import com.taobao.zeus.web.platform.client.module.word.ShellWord;
import com.taobao.zeus.web.platform.client.module.word.Word;
import com.taobao.zeus.web.platform.client.module.word.component.EditTab;

public class ToolBarContainer extends CardLayoutContainer{

	private Map<Class<? extends Word>, AbstractToolBar> bars=new HashMap<Class<? extends Word>, AbstractToolBar>();
	
	private AbstractToolBar activity=null; 
	
	private BlankToolBar blank=new BlankToolBar();
	
	public ToolBarContainer(){
		bars.put(ShellWord.class, new ShellToolBar());
		bars.put(HiveWord.class, new HiveToolBar());
	}
	
	public void onDocChange(Word word){
		if(word==null){
			setActiveWidget(blank);
		}else if(bars.get(word.getClass())!=null){
			activity=bars.get(word.getClass());
			setActiveWidget(activity);
			activity.onWordChange(word);
		}else{
			setActiveWidget(blank);
		}
	}
	
	public class BlankToolBar extends AbstractToolBar{

		public BlankToolBar(){
			add(new HTML("无可用的菜单"));
		}
		public void onWordChange(Word word) {
		}
		protected CodeMirror getCodeMirror() {
			return null;
		}
		protected FileModel getFileModel() {
			return null;
		}
		protected EditTab getEditTab() {
			return null;
		}
		
	}
}
