package com.intuit.gmail.utility;

import static com.intuit.gmail.utility.Constants.STORE_ATTACHMENT_DIR;
import static com.intuit.gmail.utility.Constants.EMAIL_ADDRESS;
import static com.intuit.gmail.utility.Constants.IMAP_HOST;
import static com.intuit.gmail.utility.Constants.IMAP_PORT;
import static com.intuit.gmail.utility.Constants.PASSWORD;
import static com.intuit.gmail.utility.Constants.SMTP_HOST;
import static com.intuit.gmail.utility.Constants.SMTP_PORT;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

import org.apache.log4j.Logger;

public class ReceiveEmailUtility {
	private static Session session;
    private static Store store;
    private Logger logger = Logger.getLogger(this.getClass());
    	
    
    public void setProperties(){
		Properties props = System.getProperties();
		props.setProperty("mail.imap.ssl.enable", "true");
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.host", SMTP_HOST);
		props.put("mail.smtp.user", EMAIL_ADDRESS);
		props.put("mail.smtp.password", PASSWORD);
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.auth", true);
		props.put("mail.imap.post", IMAP_PORT);
		props.put("mail.imap.host", IMAP_HOST);
		props.put("mail.imap.user", EMAIL_ADDRESS);
		props.put("mail.imap.password", PASSWORD);
		props.put("mail.imap.auth", true);
		session = Session.getInstance(props,
//		session = Session.getDefaultInstance(props,
	            new Authenticator() {
	                protected PasswordAuthentication  getPasswordAuthentication() {
	                    return new PasswordAuthentication(
	                            EMAIL_ADDRESS, PASSWORD);
	                }
	            });
    }
    
    public Message[] getMessages(String readFolder){
    	Message[] arrayMessages = null;
    	try {
			store = session.getStore("imaps");
			store.connect(IMAP_HOST, EMAIL_ADDRESS, PASSWORD);
			Folder testEmailFolder = store.getFolder(readFolder);
			testEmailFolder.open(Folder.READ_WRITE);
			// fetches new messages from server
			arrayMessages = testEmailFolder.getMessages();
	        
    	} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayMessages;
    }
    
    public String getEmailFromAddress(Message recentMessage){
    	String from= null;
    	try{
    	Address[] fromAddress = recentMessage.getFrom();
        from = fromAddress[0].toString();
    	}catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return from;
    }

    public String getEmailSubject(Message recentMessage){
    	String subject = "";
    	try{
    		subject = recentMessage.getSubject();
    	}catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    	return subject;
    }
    
    public Address[] getEmailToAddress(Message recentMessage){
    	Address[] toAddressList = null;
    	try{
    		toAddressList = recentMessage.getAllRecipients();
    	}catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    	return toAddressList;
    }
    
    public String getEmailSentDate(Message recentMessage){
    	String sentDate = null;
    	try{
    		sentDate = recentMessage.getSentDate().toString();
    	}catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return sentDate;
    }
    
    public String getEmailContent(Message recentMessage){
    	Object content = null;
    	try{
    		content = recentMessage.getContent();
    	}catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String body = extractMessageContent(content);
    	return body;
    }
    
    public String extractMessageContent(Object content) {
    	String body = null;
    	// Grab the body content text
	    if ( content instanceof String ) {
	    	body = (String) content;
	      System.out.println("CONTENT:" + body);
	    }
	    else if ( content instanceof Multipart ) {
	        // Make sure to cast to it's Multipart derivative
	        try {
				body = parseMultipart( (Multipart) content );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	    return body;
        }


//Parse the Multipart to find the body
public  String parseMultipart( Multipart mPart ) throws IOException, MessagingException {
	String body = null;
	String attachFiles = "" ;
	 // Loop through all of the BodyPart's
	  for ( int i = 0; i < mPart.getCount(); i++ ) {
	    // Grab the body part
	    BodyPart bp = mPart.getBodyPart( i );
	    // Grab the disposition for attachments
	    String disposition = bp.getDisposition();
//	    String disposition = mPart.getDisposition(); ****

	    MimeBodyPart mbp = (MimeBodyPart) bp;
	    // It's not an attachment
	    if ( disposition == null && bp instanceof MimeBodyPart ){
//	      MimeBodyPart mbp = (MimeBodyPart) bp;

	      // Check to see if the message text is buried in another Multipart
	      if ( mbp.getContent() instanceof Multipart ) {
	        // Use recursion to parse the sub-Multipart
	        parseMultipart( (Multipart) mbp.getContent() );
	      } else {
	        // Time to grab and edit the body
	        if ( mbp.isMimeType( "text/plain" )) {
	          // Grab the body containing the text version
	          body = (String) mbp.getContent();
	          System.out.println("body text/plain: **** \n" + body);
	        } 
//	        else if ( mbp.isMimeType( "text/html" )) {
//	          // Grab the body containing the HTML version
//	          String body = (String) mbp.getContent();
//	          System.out.println("body text/html: **** " + body);
//	          // Add our custom message to the HTML before
////	          // the closing </body>
////	          body = addStrToHtmlBody( mesgStr, body ); ***
////
////	          // Reset the content
////	          mbp.setContent( body, "text/html" ); ***
//	        }
	      }
	    }else if(disposition != null && Part.ATTACHMENT.equalsIgnoreCase(disposition)){
	    	String fileName = mbp.getFileName();
            attachFiles += fileName + ", ";
            mbp.saveFile(STORE_ATTACHMENT_DIR + File.separator + fileName);
            if (attachFiles.length() > 1) {
                attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                System.out.println("\t Attachments: " + attachFiles);
                body = attachFiles;
            }
	    	
	    }
	  }
	  return body;
 	}
}

