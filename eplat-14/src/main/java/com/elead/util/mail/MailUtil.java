package com.elead.util.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
 
public class MailUtil {
 
    static int port = 25;
    static String mailHost = "mail.e-lead.cn";//邮件服务器mail.cpip.net.cn
    static String user = "hr@e-lead.cn";//发送者邮箱地址
    static String password = "Elead2016";//密码
 
    public static void sendEmail(String subject, String body, String [] tos) {
    	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		SimpleMailMessage mm = new SimpleMailMessage();
		// 初始化JavaMailSenderImpl，当然推荐在spring配置文件中配置，这里是为了简单
	    // 设置参数
	    mailSender.setHost(mailHost);
	    mailSender.setPort(port);
	    mailSender.setUsername(user);
	    mailSender.setPassword(password);
	    mm.setFrom(user);
	    mm.setTo(tos);
	    mm.setSubject(subject);
	    mm.setText(body);
	    Properties prop = new Properties();  
        prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确  
        prop.put("mail.smtp.timeout", "25000");  
        mailSender.setJavaMailProperties(prop);  
	    mailSender.send(mm);
    }
    public static void main(String args[]) throws UnsupportedEncodingException
    {
    	String url = "http://hr.e-lead.cn:6060/mytask.html?uid=13213212";
//        sendEmail("某某某请假","<a href="+url+ ">进入</a>",new String[]{"dingl@e-lead.cn"});//收件人
        sendEmail("张XX请假","<a href="+url+ ">进入</a>",new String[]{"zhangys@e-lead.cn"});//收件人
        System.out.println("ok");
    }
}