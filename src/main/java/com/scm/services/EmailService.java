package com.scm.services;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to) {
		// TODO Auto-generated method stub
		
		boolean f = false;
		
		//Source Mail Id
		String from = "sharma.princekumar0611@gmail.com";
		
		//Variable for G-mail
		String host = "smtp.gmail.com";
		
		//Get the system properties
		Properties properties = System.getProperties();
		System.out.println("Properties : "+properties);
		
		// Setting important information to properties object
		// Host Set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		
		//Step 1: To Get Session Object
		Session session = Session.getInstance(properties,new Authenticator() {
		
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("sharma.princekumar0611@gmail.com", "wbjy aphy zvmj tohq ");
			}
		});
		session.setDebug(true);
		
		//Step 2: Compose the message (text, attachment, multimedia)
		
		MimeMessage message1 = new MimeMessage(session);
		
		try {
			//From mail Id
			message1.setFrom(from);
			//Reciepient mail id
			message1.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			//Adding text to message
//			message1.setText(message);
			message1.setContent(message, "text/html");
			
			//Adding Subject
			message1.setSubject(subject, "text/html");
			
			// Step 3 : Send the message using Transport class
			Transport.send(message1);
			
			System.out.println("Message Send Successfully");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return true;
		
	}


}
