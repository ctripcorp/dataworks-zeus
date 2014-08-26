package com.taobao.zeus.web.platform.client.util.template;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface TemplateResources extends ClientBundle{
	@Source("home.html")
	TextResource home();
	@Source("notice.html")
	TextResource notice();
}
