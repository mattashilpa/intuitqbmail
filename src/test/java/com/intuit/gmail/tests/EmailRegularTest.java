package com.intuit.gmail.tests;
import com.intuit.gmail.utility.SendEmailUtility;

import javax.mail.Message;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.api.services.gmail.Gmail;
import com.intuit.gmail.utility.ReceiveEmailUtility;
import static com.intuit.gmail.utility.Constants.INBOX_FOLDER;

public class EmailRegularTest {
	Gmail service;
	@BeforeMethod
	public void setUp(){
		service = SendEmailUtility.getGmailService();
		SendEmailUtility.sendMessage(service, "me", SendEmailUtility.createEmail(
		           "toshilpamaatta@gmail.com",
		           "fromshilpamatta@gmail.com",
		           "ShilpaMatta - Automated messages new2",
		           "This is a java program leveraging the Google Gmail API to "
		           +"send an automated message."
		       ));
	}
	@Test
	public void testEmail(){
	        
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReceiveEmailUtility emailUtility = new ReceiveEmailUtility();
		emailUtility.setProperties();
		Message[] messages = emailUtility.getMessages(INBOX_FOLDER);
		Message newMessage = messages[messages.length -1];
		System.out.println("From : " + emailUtility.getEmailFromAddress(newMessage) );
		System.out.println("Subject : " + emailUtility.getEmailSubject(newMessage));
		System.out.println("SentDate : " + emailUtility.getEmailSentDate(newMessage));
		System.out.println("Content : " + emailUtility.getEmailContent(newMessage));
	}
	
	@Test
	public void testEmailWithBlankSubject(){
	        
		SendEmailUtility.sendMessage(service, "me", SendEmailUtility.createEmail(
           "toshilpamaatta@gmail.com",
           "fromshilpamatta@gmail.com",
           "",
           "This is a java program leveraging the Google Gmail API to "
           +"send an automated message."
       ));
		ReceiveEmailUtility emailUtility = new ReceiveEmailUtility();
		emailUtility.setProperties();
		Message[] messages = emailUtility.getMessages(INBOX_FOLDER);
		Message newMessage = messages[messages.length -1];
		System.out.println("From : " + emailUtility.getEmailFromAddress(newMessage) );
		System.out.println("Subject : " + emailUtility.getEmailSubject(newMessage));
		System.out.println("SentDate : " + emailUtility.getEmailSentDate(newMessage));
		System.out.println("Content : " + emailUtility.getEmailContent(newMessage));
	}
	
	@Test
	public void testEmailWithBlankBody(){
	        
		SendEmailUtility.sendMessage(service, "me", SendEmailUtility.createEmail(
           "toshilpamaatta@gmail.com",
           "fromshilpamatta@gmail.com",
           "ShilpaMatta - Blank Body",
           ""
       ));
		ReceiveEmailUtility emailUtility = new ReceiveEmailUtility();
		emailUtility.setProperties();
		Message[] messages = emailUtility.getMessages(INBOX_FOLDER);
		Message newMessage = messages[messages.length -1];
		System.out.println("From : " + emailUtility.getEmailFromAddress(newMessage) );
		System.out.println("Subject : " + emailUtility.getEmailSubject(newMessage));
		System.out.println("SentDate : " + emailUtility.getEmailSentDate(newMessage));
		System.out.println("Content : " + emailUtility.getEmailContent(newMessage));
	}
}
