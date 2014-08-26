package com.taobao.zeus.web.platform.client.lib.codemirror;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;

public class CodeMirror extends Widget{

	public static String theme="default";
	static{
		CodeMirrorCSS css=GWT.create(CodeMirrorCSS.class);
		css.style().ensureInjected();
	}
	private JavaScriptObject cm;
	
	public static interface OnChangeListener{
		void onChange();
	}
	
	private List<OnChangeListener> listeners=new ArrayList<OnChangeListener>();
	private List<MouseUpHandler> mouseListeners=new ArrayList<MouseUpHandler>();
	public CodeMirror(final CodeMirrorConfig config){
		setElement(DOM.createDiv());
		addAttachHandler(new Handler() {
			public void onAttachOrDetach(AttachEvent event) {
				if(event.isAttached()){
					cm=create(getElement(), config.toJsObject(CodeMirror.this));
				}
			}
		});
		sinkEvents(Event.MOUSEEVENTS|Event.ONCLICK);
	}
	
	public void addChangeListener(OnChangeListener listener){
		listeners.add(listener);
	}
	public void removeChangeListener(OnChangeListener listener){
		listeners.remove(listener);
	}
	public void addMouseUpHandler(MouseUpHandler handler){
		mouseListeners.add(handler);
	}
	public void removeMouseUpHandler(MouseUpHandler handler){
		mouseListeners.remove(handler);
	}
	@Override
	public void onBrowserEvent(Event event) {
		switch(DOM.eventGetType(event)){
		case Event.ONMOUSEUP:
		case Event.ONMOUSEOVER:
		case Event.ONCLICK:
			for(MouseUpHandler h:mouseListeners){
				h.onMouseUp(null);
			}
		}
	}
	
	public native void autoLoadMode(String type)/*-{
		var cm=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm;
		$wnd.CodeMirror.autoLoadMode(cm,type);
	}-*/;
	
	public native String getValue()/*-{
		return this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.getValue();
	}-*/;
	
	public native void setValue(String value)/*-{
		if(value==null){
			value=" ";
		}
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.setValue(value);
	}-*/;
	
	public native String getSelection()/*-{
		return this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.getSelection();
	}-*/;
	
	public native void replaceSelection(String value)/*-{
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.replaceSelection(value);
	}-*/;
	
	public native void setSize(String width,String height)/*-{
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.setSize(width,height);
	}-*/;

	public native void focus()/*-{
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.focus();
	}-*/;
	
	public native void scrollTo(int x,int y)/*-{
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.scrollTo(x,y);
	}-*/;
	
	public native void setOption(String option,String value)/*-{
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.setOption(option,value);
	}-*/;
	
	public native String getOption(String option)/*-{
		return this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.getOption(option);
	}-*/;
	
	public native void undo()/*-{
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.undo();
	}-*/;
	
	public native void redo()/*-{
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.redo();
	}-*/;
	
	public native int lineCount()/*-{
		return this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.lineCount();
	}-*/; 
	
	public native void refresh()/*-{
		this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm.refresh();
	}-*/;
	
	public native void format()/*-{
		var editor=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm;
		$wnd.CodeMirror.commands['selectAll'](editor);
		editor.autoFormatRange(editor.getCursor(true),editor.getCursor(false));
	}-*/;
	
	private static native JavaScriptObject create(Element el,JavaScriptObject config)/*-{
		var editor=$wnd.CodeMirror(el,config);
		editor.setOption('theme',@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::theme);
		return editor;
	}-*/;
	
	public static class CodeMirrorConfig{
		public String value="";
		public String mode;
		public String theme;
		public int indentUnit=2;
		public boolean smartIndent;
		public int tabSize=4;
		public boolean indentWithTabs;
		public boolean electricChars=true;
		public boolean autoClearEmptyLines;
		public String keyMap;
		public Object extraKeys;
		public boolean lineWrapping;
		public boolean lineNumbers=true;
		public int firstLineNumber=1;
		public boolean gutter;
		public boolean fixedGutter;
		public boolean readOnly=true;
		
		public native JavaScriptObject toJsObject(CodeMirror cm)/*-{
			var js={};
			if(this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::value!=null){
				js.value=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::value;
			}
			if(this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::mode!=null){
				js.mode=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::mode;
			}
			if(this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::theme!=null){
				js.theme=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::theme;
			}
			js.indentUnit=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::indentUnit;
			js.smartIndent=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::smartIndent;
			js.tabSize=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::tabSize;
			js.indentWithTabs=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::indentWithTabs;
			js.electricChars=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::electricChars;
			js.autoClearEmptyLInes=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::autoClearEmptyLines;
			if(this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::keyMap!=null){
				js.keyMap=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::keyMap;
			}
			if(this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::extraKeys!=null){
				js.extraKeys=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::extraKeys;
			}
			js.lineWrapping=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::lineWrapping;
			js.lineNumbers=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::lineNumbers;
			js.firstLineNumber=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::firstLineNumber;
			js.gutter=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::gutter;
			js.fixedGutter=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::fixedGutter;
			js.readOnly=this.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror.CodeMirrorConfig::readOnly;
			
			if(cm!=null){
				js.onChange=function(){
					cm.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::change(Ljava/lang/String;)('');
				}
				js.onCursorActivity=function(){
					var editor=cm.@com.taobao.zeus.web.platform.client.lib.codemirror.CodeMirror::cm;
					if($wnd.hlLine!=null){
						editor.setLineClass($wnd.hlLine,null,null);
					}
					$wnd.hlLine=editor.setLineClass(editor.getCursor().line,null,"activeline");
				}
			}
			return js;
		}-*/;
	}
	
	public void change(String msg){
		for(OnChangeListener lis:listeners){
			lis.onChange();
		}
	}
	
	public interface CodeMirrorCSS extends ClientBundle{
		@Source("CodeMirror.css")
		@NotStrict
		CssResource style();
	}

}
