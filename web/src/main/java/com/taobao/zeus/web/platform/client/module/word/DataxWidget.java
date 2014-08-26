package com.taobao.zeus.web.platform.client.module.word;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.taobao.zeus.web.platform.client.util.RPCS;

public class DataxWidget extends Widget{
	private static DataxWidget instance;
	private static Callback callback;
	public static DataxWidget getInstance(Callback callback){
		if(instance==null){
			instance=new DataxWidget();
			imbedCallback();
		}
		DataxWidget.callback=callback;
		return instance;
	}
	
	private DataxWidget(){
		setElement(DOM.createIFrame());
	}
	
	public void setContent(String html){
		fillIframe((IFrameElement)getElement().cast(), html);
	}
	
	private static native void imbedCallback()/*-{
		$wnd.dataxcallback=function(id){
			var callback=@com.taobao.zeus.web.platform.client.module.word.DataxWidget::callback;
			callback.@com.taobao.zeus.web.platform.client.module.word.DataxWidget.Callback::callback(I)(id);
		};
	}-*/;
	
	private final native void fillIframe(IFrameElement iframe, String content) /*-{
	  var doc = iframe.document;
	 
	  if(iframe.contentDocument)
	    doc = iframe.contentDocument; // For NS6
	  else if(iframe.contentWindow)
	    doc = iframe.contentWindow.document; // For IE5.5 and IE6
	 
	   // Put the content in the iframe
	  doc.open();
	  doc.writeln(content);
	  doc.close();
	}-*/;
	
	public interface Callback{
		public void callback(int id);
	}
}
