/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoit;

/**
 *
 * @author remi
 */
import java.io.*;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Date;

import javax.mail.*;

import javax.mail.internet.*;

import com.sun.mail.smtp.*;
import java.util.ArrayList;


public class MailSender {
    public static void sendEmail(String subject, String body, ArrayList<String> recipients) {
        Properties props = System.getProperties();
        props.put("mail.smtps.host","smtp.critsend.com");
        props.put("mail.smtps.auth","true");
		
        Session session = Session.getInstance(props, null);
		
		try {
			InternetAddress from = new InternetAddress("admin@sopra-covoit.fr");
			SMTPTransport t = (SMTPTransport)session.getTransport("smtps");
			t.connect("smtp.critsend.com", "saurel@etud.insa-toulouse.fr", "t1R352wq7mOke7rUE6oh");

			for(String recipient : recipients) {
				try {
					Message msg = new MimeMessage(session);
					msg.setFrom(from);
					msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));

					msg.setSubject(subject);
					msg.setText(body);
					//msg.setHeader("X-Mailer", "Header");

					msg.setSentDate(new Date());
					t.sendMessage(msg, msg.getAllRecipients());
				}
				catch(MessagingException e) {}
			}
			t.close();
		}
		catch(MessagingException e) {
			
		}
     }
}