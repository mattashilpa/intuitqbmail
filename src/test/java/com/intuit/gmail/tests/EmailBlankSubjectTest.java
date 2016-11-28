package com.intuit.gmail.tests;
import com.intuit.gmail.utility.SendEmailUtility;

import javax.mail.Message;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.api.services.gmail.Gmail;
import com.intuit.gmail.utility.ReceiveEmailUtility;
import static com.intuit.gmail.utility.Constants.INBOX_FOLDER;


public class EmailBlankSubjectTest {
	Gmail service;
	@BeforeMethod
	public void setUp(){
		service = SendEmailUtility.getGmailService();
		SendEmailUtility.sendMessage(service, "me", SendEmailUtility.createEmail(
		           "toshilpamaatta@gmail.com",
		           "fromshilpamatta@gmail.com",
		           "",
		           "This is a java program leveraging the Google Gmail API to "
		           +"send an automated message."
		       ));
	}
	
	@Test
	public void testEmailWithBlankSubject(){
		ReceiveEmailUtility emailUtility = new ReceiveEmailUtility();
		emailUtility.setProperties();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message[] messages = emailUtility.getMessages(INBOX_FOLDER);
		Message newMessage = messages[messages.length -1];
		System.out.println("From : " + emailUtility.getEmailFromAddress(newMessage) );
		Assert.assertEquals(emailUtility.getEmailFromAddress(newMessage), "fromshilpamatta@gmail.com", "Unexpected Email From Address");
		System.out.println("Subject : " + emailUtility.getEmailSubject(newMessage));
		Assert.assertEquals(emailUtility.getEmailSubject(newMessage), null, "Unexpected Email Subject");
		System.out.println("SentDate : " + emailUtility.getEmailSentDate(newMessage));
		System.out.println("Content : " + emailUtility.getEmailContent(newMessage));
		Assert.assertEquals(emailUtility.getEmailContent(newMessage).replace("/\n", "").trim(), "This is a java program leveraging the Google Gmail API to send an automated message.", "Unexpected Email Content");
	}
	
	
}
