package com.taobao.zeus.web.platform.client.app.user;

import com.taobao.zeus.web.platform.client.theme.ResourcesTool;
import com.taobao.zeus.web.platform.client.widget.Shortcut;

public class UserShortcut extends Shortcut{

	public UserShortcut(){
		super("user", "用户管理");
		setIcon(ResourcesTool.iconResources.user());
	}


}
