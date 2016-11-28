package com.intuit.gmail.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;


public class SendEmailUtility {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Gmail API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
        Arrays.asList(GmailScopes.GMAIL_COMPOSE);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(){
		String resourcePath = "client_secret.json";
		resourcePath = resourcePath.replace('/', File.separatorChar);
        InputStream in=null;
        GoogleClientSecrets clientSecrets;
        GoogleAuthorizationCodeFlow flow;
        Credential credential = null;
		try {
			// Load client secrets.
//			in = GmailQuickStart.class.getResourceAsStream(resourcePath);
			in = new FileInputStream(new File(resourcePath));
//			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
//			          new InputStreamReader(GmailQuickStart.class.getResourceAsStream("resourcePath")));
			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileInputStream(new File(resourcePath.replace('/', File.separatorChar))));
			// Build flow and trigger user authorization request.
			flow = new GoogleAuthorizationCodeFlow.Builder(
			        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
			.setDataStoreFactory(DATA_STORE_FACTORY)
			.setAccessType("offline")
			.build();
		
        credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService(){
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    /**
    * Send an email from the user's mailbox to its recipient.
    *
    * @param service Authorized Gmail API instance.
    * @param userId User's email address. The special value "me"
    * can be used to indicate the authenticated user.
    * @param email Email to be sent.
    * @throws MessagingException
    * @throws IOException
    */
    public static void sendMessage(Gmail service, String userId, MimeMessage email){
        Message message;
		try {
			message = createMessageWithEmail(email);
		
	        message = service.users().messages().send(userId, message).execute();
	
	        System.out.println("Message id: " + message.getId());
	        System.out.println(message.toPrettyString());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
    * Create a Message from an email
    *
    * @param email Email to be set to raw of message
    * @return Message containing base64url encoded email.
    * @throws IOException
    * @throws MessagingException
    */
    public static Message createMessageWithEmail(MimeMessage email)
      throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
    * Create a MimeMessage using the parameters provided.
    *
    * @param to Email address of the receiver.
    * @param from Email address of the sender, the mailbox account.
    * @param subject Subject of the email.
    * @param bodyText Body text of the email.
    * @return MimeMessage to be used to send email.
    * @throws MessagingException
    */
    public static MimeMessage createEmail(String to, String from, String subject,
      String bodyText) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        try{
        InternetAddress tAddress = new InternetAddress(to);
        InternetAddress fAddress = new InternetAddress(from);

        email.setFrom(fAddress);
        email.addRecipient(javax.mail.Message.RecipientType.TO,tAddress);
        email.setSubject(subject);
        email.setText(bodyText);
        } catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return email;
    }
    
    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to Email address of the receiver.
     * @param from Email address of the sender, the mailbox account.
     * @param subject Subject of the email.
     * @param bodyText Body text of the email.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
     public static MimeMessage createEmailWithMultipleRecipients(String[] toList, String from, String subject,
       String bodyText) {
         Properties props = new Properties();
         Session session = Session.getDefaultInstance(props, null);

         MimeMessage email = new MimeMessage(session);
         try{         
         InternetAddress fAddress = new InternetAddress(from);
         email.setFrom(fAddress);
         for(String to : toList){
        	 email.addRecipient(javax.mail.Message.RecipientType.TO,
                            new InternetAddress(to));
         }
         email.setSubject(subject);
         email.setText(bodyText);
         } catch (MessagingException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} 
         return email;
     }
     
    /**
    * Create a MimeMessage using the parameters provided.
    *
    * @param to Email address of the receiver.
    * @param from Email address of the sender, the mailbox account.
    * @param subject Subject of the email.
    * @param bodyText Body text of the email.
    * @param fileDir Path to the directory containing attachment.
    * @param filename Name of file to be attached.
    * @return MimeMessage to be used to send email.
    * @throws MessagingException
    */
    public static MimeMessage createEmailWithAttachment(String to, String from, String subject,
      String bodyText, String fileDir, String filename)  {
        Properties props = new Properties();
        Session session ;
        MimeMessage email = null ;
        InternetAddress tAddress ;
        InternetAddress fAddress ;
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        try{
        session = Session.getDefaultInstance(props, null);

        email = new MimeMessage(session);
        tAddress = new InternetAddress(to);
        fAddress= new InternetAddress(from);

        email.setFrom(fAddress);
        email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
        email.setSubject(subject);

//        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");
        mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

//        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(fileDir + filename);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(filename);
        String contentType = Files.probeContentType(FileSystems.getDefault()
            .getPath(fileDir, filename));
        mimeBodyPart.setHeader("Content-Type", contentType + "; name=\"" + filename + "\"");
        mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");

        multipart.addBodyPart(mimeBodyPart);

        email.setContent(multipart);
        }catch(MessagingException me){
        	me.printStackTrace();
        }catch(IOException io){
        	io.printStackTrace();
        }
        return email;
    }

    public static void printLabels(Gmail service, String paramUser) 
        throws IOException {
        // Print the labels in the user's account.
        String user = paramUser;
        ListLabelsResponse listResponse =
            service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.size() == 0) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }
    }



//    public static void main(String[] args) throws Exception {
//        // Build a new authorized API client service.
//        Gmail service = getGmailService();
//        
////        sendMessage(service, "me", createEmail(
////            "toshilpamaatta@gmail.com",
////            "fromshilpamatta@gmail.com",
////            "ShilpaMatta - Automated messages3",
////            "This is a java program leveraging the Google Gmail API to "
////            +"send an automated message."
////        ));
//        
////        sendMessage(service,"me",createEmailWithAttachment(
////        		"toshilpamaatta@gmail.com",
////                "fromshilpamatta@gmail.com",
////                "ShilpaMatta - Automated messages with attachment",
////                "This is a java program leveraging the Google Gmail API to "
////                +"send an automated message with attachment.", 
////                "F:\\shilpa\\Attachment\\", "testAttach.txt"));
//        
////        sendMessage(service,"me",createEmail(
////        		"toshilpamaatta@gmail.com",
////                "fromshilpamatta@gmail.com",
////                "",
////                "This is a java program without subject."));
//        
////        sendMessage(service,"me",createEmail(
////        		"",
////                "",
////                "Automated email",
////                "This is a java program without subject."));
//        
////        sendMessage(service,"me",createEmail(
////        		"toshilpamaatta@gmail.com",
////                "fromshilpamatta@gmail.com",
////                "Automated Email - blank email",
////                ""));
//        
//        sendMessage(service,"me",createEmail(
//        		"toinvalidshilpamaatta@gmail.com",
//                "fromshilpamatta@gmail.com",
//                "Automated Email - invalid email",
//                "Invalid email address"));
//    
//    }
//    
//    

}