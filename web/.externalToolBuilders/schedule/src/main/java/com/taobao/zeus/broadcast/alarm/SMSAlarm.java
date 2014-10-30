package com.taobao.zeus.broadcast.alarm;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.util.Environment;
import com.taobao.zeus.util.JsonUtil;


public class SMSAlarm extends AbstractZeusAlarm{
	private static Logger log=LogManager.getLogger(SMSAlarm.class);
	@Autowired
	private UserManager userManager;
	
    private static String notifyUrl = Environment.getNotifyUrl();//Noc服务器
	private static String accessToken = Environment.getAccessToken();//Noc access_token

	@Override
	public void alarm(List<String> uids, String title, String content)
			throws Exception {
		String srcId = "BI-Zeus调度系统";
		String devId = InetAddress.getLocalHost().getHostName();
		String itemId = title;
		String level = "high";
		String message = title;
		List<ZeusUser> userList = userManager.findListByUid(uids);
		if(userList != null && userList.size()>0){
			for(ZeusUser user : userList){
				message += "<br/>负责人："+user.getName()+" 电话："+user.getPhone()+" 邮箱："+user.getEmail();
			}
		}
		int code = sendNOCAlarm(notifyUrl, accessToken, srcId, devId, itemId, level, message);
//		System.out.println("send cats code:" + code);
	}

	@SuppressWarnings("deprecation")
	public Integer sendNOCAlarm(String sendUrl, String accessToken, String srcId, String devId, String itemId, String level, String message) {
        HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(sendUrl);
		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			Map<String, String> bodyMap = new HashMap<String, String>();
			bodyMap.put("access_token", accessToken);
			bodyMap.put("request_body", "njson=" + getRequestBody(srcId, devId, itemId, level, message));
			method.setRequestBody(JsonUtil.map2json(bodyMap).toString());
			int code = client.executeMethod(method);
			if (code != 200) {
				log.error("send cats failed, code: " + code);
			}
			return code;
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		} 
   }
   
	private String getRequestBody(String srcId, String devId, String itemId, String level, String message) throws UnsupportedEncodingException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("src_id",  srcId);
		param.put("dev_id",  devId);
		param.put("item_id", itemId);
		param.put("level",   level);
		param.put("message", message);
		param.put("fields",  "[]");
//		System.out.println(JsonUtil.map2json(param).toString());
		return URLEncoder.encode(JsonUtil.map2json(param).toString(), "utf-8");
	}

}