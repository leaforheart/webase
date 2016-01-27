package com.inveno.cps.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inveno.util.PropertyUtils;
import com.inveno.util.StringUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 发送邮件工具类
 * @author Xjun
 * @date 2015-03-02
 */
public class SendMailUtil extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(SendMailUtil.class);
	
	/**
	 * 配置并发送邮件工具方法（支持批量发送，只需在Mail对象set多个收件人并用逗号分隔即可）
	 * @param mail 邮件信息对象
	 * @author Xjun
	 * @throws Exception 
	 * @date 2015-03-02
	 */
	public static void sendMail(Mail mail) throws Exception{
		
		if(null==mail || StringUtil.isEmpty(mail.getToMail())) {
			return;
		}
		
		final String invenoSysMailUserName = PropertiesUtil.getProperty("invenoSysMailUserName"); //inveno系统邮箱账号
		final String invenoSysMailPassword = PropertiesUtil.getProperty("invenoSysMailPassword"); //inveno系统邮箱密码
		final String mailHost = PropertiesUtil.getProperty("mailHost");                               //企业邮箱服务器 smtp.qq.com
		
		//创建session
		Properties props = new Properties();
       
		props.put("mail.smtp.host", mailHost);

		props.put("mail.smtp.auth", "true");
		//创建session
		Session session=Session.getInstance(props,
				new Authenticator() {
              	protected PasswordAuthentication getPasswordAuthentication() {
              		return new PasswordAuthentication(invenoSysMailUserName,invenoSysMailPassword); //添加发件人用户名与密码
              	}
          	}
		);
		
		//创建Message
		Message msg=new MimeMessage(session);
	    try{
	    	String nick=""; 
	        try { 
	            nick=javax.mail.internet.MimeUtility.encodeText("深圳市英威诺科技");
	        } catch (UnsupportedEncodingException e) { 
	            e.printStackTrace(); 
	        }  

	    	msg.setFrom(new InternetAddress(invenoSysMailUserName,nick));                       //设置发信箱
	        msg.setSubject(mail.getMailTitle());                                                //设置邮件标题
	        msg.setContent(mail.getMailBody(), "text/html;charset=utf-8");                      //发送邮件内容 ,与邮件字符编码
	        //设置批量收件人，并批量发送邮件
	        //会显示收件人，但可能会被认为是垃圾邮件
	        msg.setRecipients(RecipientType.TO, InternetAddress.parse(mail.getToMail()));
	        Transport.send(msg);
	    }catch(Exception e){
	    	log.error("英威诺科技服务邮件发送失败!"+e.getMessage()+"================接收方："+mail.getToMail());
	    	throw new Exception("邮件发送失败!");
	    }
	}
	
	/**
	 * 此方法用于测试
	 * 批量发送邮件方法
	 * @param mail 邮件信息对象
	 * @author Xjun
	 * @date 2015-03-02
	 */
	public static void sendMail4Test(Mail mail){
		
		if(null==mail || StringUtil.isEmpty(mail.getToMail())) {
			return;
		}
		
		final String invenoSysMailUserName = "service@inveno.com";//btb系统邮箱账号
		final String invenoSysMailPassword = "inveno@2015";//btb系统邮箱密码
		final String host = "smtp.qq.com";//企业邮箱服务器 smtp.qiye.163.com
		
		//创建session
		Properties props = new Properties();
       
		props.put("mail.smtp.host", host);//smtp.ym.163.com smtp.qq.com smtp.yesbtc.co

		props.put("mail.smtp.auth", "true");
		//创建session
		Session session=Session.getInstance(props,
				new Authenticator() {
              	protected PasswordAuthentication getPasswordAuthentication() {
              		return new PasswordAuthentication(invenoSysMailUserName,invenoSysMailPassword);//添加发件人用户名与密码
              	}
          	}
		);
		
		//创建Message
	       Message msg=new MimeMessage(session);
	       try{
	    	   String nick=""; 
	           try { 
	               nick=javax.mail.internet.MimeUtility.encodeText("英威诺");
	           } catch (UnsupportedEncodingException e) { 
	               e.printStackTrace(); 
	           }  

	    	   msg.setFrom(new InternetAddress(invenoSysMailUserName,nick));//设置发信箱
	           msg.setSubject(mail.getMailTitle());//设置邮件标题
	           msg.setContent(mail.getMailBody(), "text/html;charset=utf-8");//发送邮件内容 ,与邮件字符编码
	           //设置批量收件人，并批量发送邮件
	           //会显示收件人，但可能会被认为是垃圾邮件
	           msg.setRecipients(RecipientType.TO, InternetAddress.parse(mail.getToMail()));
	           Transport.send(msg);
	       }catch(Exception e){
	    	   log.error("英威诺平台邮件发送失败!"+e.getMessage()+"接收方："+mail.getToMail());
	    	   return;
	       }
	}
	
	/**
	 * 获取邮件模板
	 * @param type 模板类型
	 * @author Xjun
	 * @date 2015-03-02
	 */
	public static String getMailTemplate(int type) {
		String templateFile = "";
		if(1==type){
			templateFile = "findPwdEmail.txt";        //#找回密码邮件模板
		}else if(2==type) {
			templateFile = "registerCodeEmail.txt";
		}
		BufferedReader read = null;
		String fileName = Thread.currentThread().getContextClassLoader().getResource("/").getPath() + "emailTemplate" + File.separator + templateFile;
		fileName=fileName.replace("%20", " ");
		String s = "";
		StringBuilder sb = new StringBuilder(200);
		try {
			read = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			s = read.readLine();
			while (s != null) {					
				if (StringUtil.isNotEmpty(s)) {
					sb.append(s);
				}
				s = read.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 组装邮件内容对象并发送邮件工具方法
	 * @author Xjun
	 * @param title
	 * @param content
	 * @param receiveMail
	 * @throws Exception 
	 */
	public static void assembleMailAndSend(String title,String content,String receiveMail) throws Exception{
		Mail mail = new Mail();
		mail.setMailBody(content);
		mail.setMailTitle(title);
		mail.setToMail(receiveMail);
		SendMailUtil.sendMail(mail);
	}
	
}
