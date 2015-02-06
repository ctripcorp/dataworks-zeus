package com.taobao.zeus.web.platform.client.app.user;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class UserWidget extends Widget{

	public UserWidget(){
		setElement(DOM.createIFrame());
	}
	
	public void setContent(String html){
		fillIframe((IFrameElement)getElement().cast(), html);
	}
	
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
}
