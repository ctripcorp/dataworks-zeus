package com.taobao.zeus.web.platform.client.util;

import com.google.gwt.user.client.Window;

public class GWTEnvironment {
// 可以开启以下代码，用户判断当前环境
//	public static boolean isDaily(){
//		return Window.Location.getHost().contains("zeus.daily.taobao.net");
//	}
//	public static boolean isOnline(){
//		return Window.Location.getHost().contains("zeus.taobao.com");
//	}
	
	public static String getHomeTemplateId(){
		//TODO 请在开发中心创建文件，然后将文件id纪录于此,默认-1
		return "5";
	}
	
	public static String getNoticeTemplateId(){
		//TODO 请在开发中心创建文件，然后将文件id纪录于此,默认-1
		return "5";
	}
	
	
}
