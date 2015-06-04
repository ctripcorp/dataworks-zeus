package com.taobao.zeus.web.platform.client.app.user;

public class UserUtil {
	
	public static String getHtmlString(String str){
		if (str == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder("<div style='font-size:13px'>");
		String[] tmps = str.split("\n");
		builder.append(tmps[0]);
		for (int i=1; i<tmps.length;i++) {
			builder.append("<br>");
			builder.append(tmps[i]);
		}
		builder.append("</div>");
		return builder.toString();
	}
	
	public static final String admin = "biadmin";
}
