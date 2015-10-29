package com.bezman.Reference;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by Terence on 12/22/2014.
 */
public class EmailThread extends Thread {

    public String username, password, subject, message, recipient;

    public boolean html;

    public EmailThread(String username, String password, String subject, String message, String recipient, boolean html) {
        this.username = username;
        this.password = password;
        this.subject = subject;
        this.message = message;
        this.recipient = recipient;
        this.html = html;
    }

    @Override
    public void run() {

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.user", username);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage emailMessage = new MimeMessage(session);
        try {
            emailMessage.setFrom(new InternetAddress(username));
            emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            if (html) {
                emailMessage.setText(message, "utf-8", "html");
            }

            Transport.send(emailMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}
