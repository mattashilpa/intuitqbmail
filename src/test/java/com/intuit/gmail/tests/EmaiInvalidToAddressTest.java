package com.intuit.gmail.tests;
import javax.mail.Message;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.api.services.gmail.Gmail;
import com.intuit.gmail.common.BaseTest;
import com.intuit.gmail.utility.ReceiveEmailUtility;
import com.intuit.gmail.utility.SendEmailUtility;
import static com.intuit.gmail.utility.Constants.INBOX_FOLDER;

public class EmaiInvalidToAddressTest extends BaseTest {
	Gmail service;
	@BeforeMethod
	public void setUp(){
		service = SendEmailUtility.getGmailService();
		SendEmailUtility.sendMessage(service,"me",SendEmailUtility.createEmailWithAttachment(
        		"toinvalidaddress@gmail.com",
                "fromshilpamatta@gmail.com",
                "ShilpaMatta - Automated messages with attachment",
                "This is a java program leveraging the Google Gmail API to "
                +"send an automated message with attachment.", 
                "F:\\shilpa\\Attachment\\", "testAttach.txt"));
	}
	
	
	@Test
	public void testInvalidToAttachment(){
	     
		ReceiveEmailUtility emailUtility = new ReceiveEmailUtility();
		emailUtility.setProperties();
		Message[] messages = emailUtility.getMessages(INBOX_FOLDER);
		Message newMessage = messages[messages.length -1];
		System.out.println("From : " + emailUtility.getEmailFromAddress(newMessage) );
		Assert.assertEquals(emailUtility.getEmailFromAddress(newMessage), "fromshilpamatta@gmail.com", "Unexpected Email From Address");
		System.out.println("Subject : " + emailUtility.getEmailSubject(newMessage));
		Assert.assertEquals(emailUtility.getEmailSubject(newMessage), "ShilpaMatta - Automated messages with attachment", "Unexpected Email Subject");
		System.out.println("SentDate : " + emailUtility.getEmailSentDate(newMessage));
		System.out.println("Content : " + emailUtility.getEmailContent(newMessage));
		Assert.assertEquals(emailUtility.getEmailContent(newMessage), "testAttach.txt", "Unexpected Email Content");
	}
}
