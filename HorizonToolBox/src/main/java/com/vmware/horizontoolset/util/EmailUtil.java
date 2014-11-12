package com.vmware.horizontoolset.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.vmware.horizontoolset.email.EmailContentProps;
import com.vmware.horizontoolset.email.EmailServerProps;

public class EmailUtil {
	private static EmailServerProps _emailserverprops = new EmailServerProps();
	private static EmailContentProps contentProps = new EmailContentProps();
	private static boolean serverenabled = false;
	private static boolean contentenabled = false;
	private static Properties serverProps;
	private static Logger log = Logger.getLogger(EmailUtil.class);
	
	public static EmailServerProps getEmailServerProps(){
		return _emailserverprops;
	}
	
	public static EmailContentProps getEmailContentProps(){
		return contentProps;
		
	}
	
	public synchronized static boolean init(EmailServerProps props){
		_emailserverprops = props;
		serverProps = new Properties();
		serverProps.setProperty("mail.smtp.host", props.getMailHost());
		serverProps.setProperty("mail.host",  props.getMailHost());
		serverProps.setProperty("mail.transport.protocol", props.getProtocal());
	
		serverProps.setProperty("mail.user", props.getMailUser());
		serverProps.setProperty("mail.password", props.getMailPassword());
		serverProps.setProperty("mail.auth", String.valueOf(props.isAuth()));
		serverProps.setProperty("mail.smtp.auth",String.valueOf(props.isAuth()));
		serverenabled = true;
		return true;
	}
	
	
	public synchronized static boolean init(EmailContentProps props){
		contentProps = props;
		contentenabled = true;
		return true;
	}
	
	public static boolean isValidEmailAddress(String email) {
		   try {
		      new InternetAddress(email).validate();
		   } catch (AddressException ex) {
		      return false;
		   }
		   return true;
		}
	
	
	private static  void sendMail(String[] to, String[] cc, String[] bcc, String subject, String content) {
		
		try{
			Msg m = new Msg(to, cc, bcc, subject, content);
			
			Session session = Session.getDefaultInstance(EmailUtil.serverProps, null);
			Message msg = m.toMessage(session);

			log.info("[MailSender] Sending to: " + m.getAllRecipients());
		//	Transport.send(msg);
			 Transport transport = session.getTransport(EmailUtil.serverProps.getProperty("mail.transport.protocol"));
	         transport.connect((String) EmailUtil.serverProps.getProperty("mail.smtp.host"),EmailUtil.serverProps.getProperty("mail.user"), 
	        		 EmailUtil.serverProps.getProperty("mail.password") );
	         transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));
			log.info("[MailSender] Mail sent");
		}catch(Exception ex){
			log.error("Cant' send message due to excpetion:", ex);
		}
	}
	
	public synchronized static void sendMail() {
		if (!contentenabled || !serverenabled || contentProps==null || serverProps == null) {
			return;
		}
		String toAddress =contentProps.getToAddress();

		if (toAddress == null || toAddress.isEmpty()){
			return;
		}
		String[] tempTo = toAddress.split(",");
		List<String> listTo = new ArrayList<String>();
		for(int i=0;i<tempTo.length;i++){
			String t = tempTo[i];
			if (t==null || t.isEmpty()){
				continue;
			}
			t = t.trim();
			if (!t.isEmpty() && EmailUtil.isValidEmailAddress(t)){
				listTo.add(t);
			}
		}	
		
		String[] toall =(String[]) listTo.toArray();
		if (toall!=null && toall.length>0){
			sendMail(toall, null, null, contentProps.getTitle(), contentProps.getBody());
		}
		
	}
	
	private static final class Msg {
		String[] to;
		String[] cc;
		String[] bcc;
		String subject;
		String content;
		

		
		public Msg(String[] to, String[] cc, String[] bcc, String subject, String content) {
			super();
			this.to = to;
			this.cc = cc;
			this.bcc = bcc;
			this.subject = subject;
			this.content = content;
		}

		public String getAllRecipients() {
			StringBuilder sb = new StringBuilder();
			
			if (to != null) {
				for (String s : to)
					sb.append(s).append(';');
			}
			if (cc != null) {
				for (String s : cc)
					sb.append(s).append(';');
			}
			if (bcc != null) {
				for (String s : bcc)
					sb.append(s).append(';');
			}
			return sb.toString();
		}
		
		public Message toMessage(Session session) throws AddressException, MessagingException {

			MimeMessage mm = new MimeMessage(session);
			mm.setFrom(new InternetAddress(serverProps.getProperty("mail.host")));
			if (to != null) {
				for (String s : to)
					mm.addRecipients(RecipientType.TO, s);
			}
			if (cc != null) {
				for (String s : cc)
					mm.addRecipients(RecipientType.CC, s);
			}
			if (bcc != null) {
				for (String s : bcc)
					mm.addRecipients(RecipientType.BCC, s);
			}

			mm.setSubject(subject);
			mm.setContent(content, "text/html; charset=UTF-8");	//"text/plain"
			return mm;
		}
	}
	
}
