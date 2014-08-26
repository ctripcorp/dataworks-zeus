package com.taobao.zeus.web.platform.client.lib.codemirror;

import com.google.gwt.dom.client.Element;

public class CodeMirrors {

	public static native void runMode(String text,String mode,Element output)/*-{
		if(text!=null){
			$wnd.CodeMirror.runMode(text,mode,output);
		}
	}-*/;
	
}
