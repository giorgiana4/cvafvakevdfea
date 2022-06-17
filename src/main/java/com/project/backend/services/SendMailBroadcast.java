package com.project.backend.services;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

public class SendMailBroadcast {
    private final Properties props;
    private final String ADMIN_EMAIL = "tennis@court.com";
    private final String SUBJECT = "Find player";
    private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final SecureRandom rnd;
    private Map<String, String> credentials;
    private List<String> emails;

    public SendMailBroadcast() {
        this.props = new Properties();
        this.rnd = new SecureRandom();
        this.credentials = new HashMap<>();
        this.emails = new ArrayList<>();
        configureProps();
        configureMap();
        configureEmails();
    }

    private void configureMap() {
        credentials.put("99002cd62eadab", "12f395fdbb9c9a");
        credentials.put("447ac9a020d194", "9c4b67138c9c96");
        credentials.put("52768fa0690397", "f2752176607ab8");
    }

    private void configureProps(){
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", "smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");
    }

    private void configureEmails(){
        emails.add("iamnitchi.loredana@gmail.com");
        emails.add("firte.catalin@gmail.com");
        emails.add("proiectdisi@gmail.com");
    }

    public String sendEmail(String username, String location, LocalDate date, int hour) {
       String code = generateRandomString(rnd.nextInt(AB.length()));
       String message = "Hello! " + "\n \n" + username+ " is looking for a partner, on " + date.toString() +
               " at " + hour +". The address of the location is: "+ location + "." +
               "\nIf you want to join him, here is the code that you need to use => " + code +
               "\n \nIf you are not interested just ignore this mail.";
       sendMailBroadcast(message);

       return code;
    }

    private void sendMailBroadcast(String content) {
        int counter = 0;
        for (Map.Entry<String, String> stringStringEntry : credentials.entrySet()) {
            javax.mail.Session session = javax.mail.Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new javax.mail.PasswordAuthentication(((Map.Entry) stringStringEntry).getKey().toString(),
                                    ((Map.Entry) stringStringEntry).getValue().toString());
                        }
                    });
            try {
                javax.mail.Message message = new MimeMessage(session);

                message.setFrom(new InternetAddress(ADMIN_EMAIL));
                message.setRecipients(MimeMessage.RecipientType.TO,
                        InternetAddress.parse(emails.get(counter)));
                message.setSubject(SUBJECT);
                message.setText(content);

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            counter++;
        }
    }

    private String generateRandomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
