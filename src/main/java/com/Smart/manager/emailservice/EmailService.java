package com.Smart.manager.emailservice;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	public void sendmail(String to,String message, String subject) {
		
		  // Recipient's email ID needs to be mentioned.
       

        // Sender's email ID needs to be mentioned
        String from = "jeebachhprasad1996@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("jeebachhprasad881@gmail.com","mithukumar@123");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message1 = new MimeMessage(session);

            // Set From: header field of the header.
            message1.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message1.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message1.setSubject(subject);

            // Now set the actual message
            message1.setContent(message,"text/html");

            System.out.println("sending...");
            // Send message
            Transport.send(message1);
           
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
		
		
		
		
		
	}

}
