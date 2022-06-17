package com.project.backend.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.project.backend.dtos.ReservationDetailsDTO;
import com.project.backend.dtos.SubscriptionDetailsDTO;
import com.project.backend.entities.Reservation;
import com.project.backend.entities.Subscription;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class SendBillService {
    private final String USERNAME_AUTH = "99002cd62eadab";
    private final String PASSWORD_AUTH = "12f395fdbb9c9a";
    private final String ADMIN_EMAIL = "tennis@court.com";
    private final String SUBJECT = "Receipt";
    private final Properties props;
    private final Multipart multipart;

    public SendBillService() {
        this.props = new Properties();
        this.multipart = new MimeMultipart();
        configureProps();
    }

    private void configureProps(){
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", "smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");
    }

    public void sendEmailWithBill(String username, String locationName, String email, String nameBase) {

        String message = "Hello, " + username + "! \n \nYou can find your bill down below!  "
                + "\nHope to have a very good time alongside your friends!" + "\n \nSincerely, " +
                "\n" + locationName;
        sendMail(email, username, message, nameBase);
    }

    private void sendMail(String to, String username, String content, String nameBase) {
        javax.mail.Session session = javax.mail.Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(USERNAME_AUTH, PASSWORD_AUTH);
                    }
                });
        try {
            javax.mail.Message message = new MimeMessage(session);

            setAttachment(nameBase, username);
            setMessage(content);

            message.setContent(getMultipart());
            message.setSentDate(new Date());
            message.setFrom(new InternetAddress(ADMIN_EMAIL));
            message.setRecipients(MimeMessage.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(SUBJECT);

            Transport.send(message);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setMessage(String content) throws MessagingException {
        BodyPart messageBodyPart1 = new MimeBodyPart();
        messageBodyPart1.setText(content);

        getMultipart().addBodyPart(messageBodyPart1);
    }

    private void setAttachment(String nameBase, String username) throws MessagingException, IOException {
        MimeBodyPart attachment = new MimeBodyPart();
        String path = "src/main/resources/Pdf" + "/" + nameBase + "_" + username + "_" + LocalDate.now() + ".pdf";
        attachment.attachFile(new File(path).getCanonicalPath());

        getMultipart().addBodyPart(attachment);
    }

    private Multipart getMultipart() {
        return multipart;
    }

    private void generatePdf(String content, String nameBase, String username) {
        Document doc = new Document();
        try
        {
            String path = "src/main/resources/Pdf" + "/" + nameBase + "_" + username + "_" + LocalDate.now() + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));
            System.out.println("PDF created.");
            doc.open();
            doc.add(new Paragraph(content));
            doc.close();
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void generateReservationBill(Reservation reservation) {
        int courtNumber = reservation.getCourt().getCourtNumber();
        String content = "\n========== RESERVATION BILL ==========\n";
        content += "\nUsername: " + reservation.getClient().getUser().getUsername();
        content += "\nCourt number: " + courtNumber;
        content += "\nPlaying date: " + reservation.getDate() + ", " + reservation.getDate().getDayOfWeek();
        content += "\nStart time: " + reservation.getStartTime() + ":00";
        content += "\nEnd time: " + reservation.getEndTime() + ":00";
        content += "\n\nTotal price: " + reservation.getTotalPrice();
        generatePdf(content, "ReservationReceipt", reservation.getClient().getUser().getUsername());
    }

    public void generateSubscriptionBill(Subscription subscription) {
        int courtNumber = subscription.getCourt().getCourtNumber();
        String content = "\n========== SUBSCRIPTION BILL ==========\n";
        content += "\nUsername: " + subscription.getClient().getUser().getUsername();
        content += "\nCourt number: " + courtNumber;
        content += "\nStarting date: from " + subscription.getStartDate();
        content += "\nPlaying day: every " + subscription.getStartDate().getDayOfWeek();
        content += "\nEnding date: to " + subscription.getStartDate().plusMonths(1).minusDays(1);
        content += "\nStart time: " + subscription.getStartTime() + ":00";
        content += "\nEnd time: " + subscription.getEndTime() + ":00";
        content += "\n\nTotal price: " + subscription.getTotalPrice();
        generatePdf(content, "SubscriptionReceipt", subscription.getClient().getUser().getUsername());
    }
}
