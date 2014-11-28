package com.taobao.zeus.broadcast.alarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
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
				message += "<br>负责人："+user.getName()+" 电话："+user.getPhone()+" 邮箱："+user.getEmail();
			}
		}
		message += "<br>" + content;
		sendNOCAlarm(notifyUrl, accessToken, srcId, devId, itemId, level, message);
	}

	@SuppressWarnings("deprecation")
	public void sendNOCAlarm(String sendUrl, String accessToken, String srcId, String devId, String itemId, String level, String message) {
		log.info("begin to send the noc, the srcId is " + srcId + ", the devId is " + devId + ", the itemId is " + itemId + ".");
		log.info("the message is " + message);
		log.info("the sendUrl is " + sendUrl);
        HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(sendUrl);
		Gson gson = new Gson();
		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			Map<String, String> bodyMap = new HashMap<String, String>();
			bodyMap.put("access_token", accessToken);
			String requestBody = getRequestBody(srcId, devId, itemId, level, message);
			bodyMap.put("request_body", "njson=" + requestBody);
			method.setRequestBody(JsonUtil.map2json(bodyMap).toString());
			int code = client.executeMethod(method);
			log.info("the return code is " + HttpStatus.SC_OK);
			String responseBodyAsString = method.getResponseBodyAsString(2000);
			log.info("the return is " + responseBodyAsString);
			ReturnJson rJ = null;
			if (responseBodyAsString != null) {
				rJ = gson.fromJson(responseBodyAsString, ReturnJson.class);
			}
			if (code !=  HttpStatus.SC_OK || rJ == null || !rJ.isSuccess()) {
				log.error("send noc failed, code: " + code);
				return;
			}
			log.info("Send noc successfully!");
		} catch(HttpException  e) {
			log.error("send noc fail,", e);
		} catch (IOException e) {
			log.error("send noc fail,", e);
		} catch (Exception e) {
			log.error("send noc fail,", e);
		}
   }
   
	private String getRequestBody(String srcId, String devId, String itemId, String level, String message) throws UnsupportedEncodingException {
		Map<String, String> param = new HashMap<String, String>();
		param.put("src_id",  srcId);
		param.put("dev_id",  devId);
		param.put("item_id", itemId);
		param.put("level",   level);
		param.put("message", message);
		param.put("fields",  "");
//		System.out.println(JsonUtil.map2json(param).toString());
		return URLEncoder.encode(JsonUtil.map2json(param).toString(), "utf-8");
	}
	
	class ReturnJson{

		private String message;
		private String data;
		private boolean success;
		private int error;
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public int getError() {
			return error;
		}
		public void setError(int error) {
			this.error = error;
		}

		@Override
		public String toString() {
			return "ReturnJson [message=" + message + ", data=" + data
					+ ", success=" + success + ", error=" + error + "]";
		}
	}
	
	public static void main(String[] args) {
		String returnString = "{\"message\": \"\", \"data\": \"enqueue\", \"success\": true, \"error\": 0}";
//		String returnString = null;
		Gson gson = new Gson();
		ReturnJson rJson = gson.fromJson(returnString, ReturnJson.class);
		System.out.println("the message is " + rJson.getMessage());
		System.out.println("the data is " + rJson.getData());
		System.out.println("the error is " + rJson.getError());
		System.out.println("the success is " + rJson.isSuccess());

	}
	
	

}