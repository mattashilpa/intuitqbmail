package com.intuit.gmail.tests;
import javax.mail.Address;
import javax.mail.Message;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.api.services.gmail.Gmail;
import com.intuit.gmail.utility.ReceiveEmailUtility;
import com.intuit.gmail.utility.SendEmailUtility;

import static com.intuit.gmail.utility.Constants.INBOX_FOLDER;
public class EmailWithMultipleRecipientsTest {
	Gmail service;
	@BeforeMethod
	public void setUp(){
		service = SendEmailUtility.getGmailService();
		String[] toList = {"toshilpamaatta@gmail.com","mattashilpa@gmail.com"};
		SendEmailUtility.sendMessage(service, "me", SendEmailUtility.createEmailWithMultipleRecipients(
					toList,
		           "fromshilpamatta@gmail.com",
		           "Multiple Recipients",
		           "This is a java program leveraging the Google Gmail API to "
		           +"send an automated message."
		       ));
	}
	@Test
	public void testEmailWithMultipleRecipients(){
	        
		
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
		Assert.assertEquals(emailUtility.getEmailFromAddress(newMessage), "fromshilpamatta@gmail.com", "Unexpected Email From Address");
		System.out.println("Subject : " + emailUtility.getEmailSubject(newMessage));
		Assert.assertEquals(emailUtility.getEmailSubject(newMessage), "Multiple Recipients", "Unexpected Email Subject");
		System.out.println("SentDate : " + emailUtility.getEmailSentDate(newMessage));
		System.out.println("Content : " + emailUtility.getEmailContent(newMessage));
//		Assert.assertEquals(emailUtility.getEmailContent(newMessage).replace("/\n", "").trim(), "This is a java program leveraging the Google Gmail API to send an automated message.", "Unexpected Email Content");
	
		Address[] toAddressList = emailUtility.getEmailToAddress(newMessage);
		Assert.assertEquals(toAddressList[0].toString(),"toshilpamaatta@gmail.com");
		Assert.assertEquals(toAddressList[1].toString(),"mattashilpa@gmail.com");
	}
	
}
