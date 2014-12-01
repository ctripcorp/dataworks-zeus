package com.taobao.zeus.broadcast.alarm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
import com.taobao.zeus.util.Environment;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailAlarm extends AbstractZeusAlarm {
	@Autowired
	private UserManager userManager;
	private static Logger log = LoggerFactory.getLogger(MailAlarm.class);
	private static String host = Environment.getHost();// 邮件服务器
	private static String port = Environment.getPort();// 端口
	private static String from = Environment.getSendFrom();// 发送者
	private static String user = Environment.getUsername();// 用户名
	private static String password = Environment.getPassword();// 密码

	@Override
	public void alarm(String jobId, List<String> users, String title, String content)
			throws Exception {
		List<ZeusUser> userList = userManager.findListByUid(users);
		List<String> emails = new ArrayList<String>();
		if (userList != null && userList.size() > 0) {
			for (ZeusUser user : userList) {
				String userEmail = user.getEmail();
				if (userEmail != null && !userEmail.isEmpty()
						&& userEmail.contains("@")) {
					if (userEmail.contains(";")) {
						String[] userEmails = userEmail.split(";");
						for (String ems : userEmails) {
							if (ems.contains("@")) {
								emails.add(ems);
							}
						}
					} else {
						emails.add(userEmail);
					}
				}
			}
			if (emails.size() > 0) {
				content = content.replace("<br/>", "\r\n");
				sendEmail(jobId, emails, title, content);
			}
		}
	}

	public static void sendEmail(String jobId, List<String> emails, String subject,
			String body) {
		try {
			log.info( "jobId: " + jobId +" begin to send the email!");
			Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");
			Transport transport = null;
			Session session = Session.getDefaultInstance(props, null);
			transport = session.getTransport("smtp");
			transport.connect(host, user, password);
			MimeMessage msg = new MimeMessage(session);
			msg.setSentDate(new Date());
			InternetAddress fromAddress = new InternetAddress(from);
			msg.setFrom(fromAddress);
			InternetAddress[] toAddress = new InternetAddress[emails.size()];
			for (int i = 0; i < emails.size(); i++) {
				toAddress[i] = new InternetAddress(emails.get(i));
			}
			msg.setRecipients(Message.RecipientType.TO, toAddress);
			msg.setSubject(subject, "UTF-8");
			msg.setText(body, "UTF-8");
			msg.saveChanges();
			transport.sendMessage(msg, msg.getAllRecipients());
			log.info("jobId: " + jobId + " send email: " + emails + "; from: " + from + " subject: "
					+ subject + ", send success!");
		} catch (NoSuchProviderException e) {
			log.error("jobId: " + jobId + " fail to send the mail. ", e);
		} catch (MessagingException e) {
			log.error("jobId: " + jobId + " fail to send the mail. ", e);
		} catch (Exception e) {
			log.error("jobId: " + jobId + " fail to send the mail. ", e);
		}
	}
}
