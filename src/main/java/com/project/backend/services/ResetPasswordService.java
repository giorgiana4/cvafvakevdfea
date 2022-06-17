package com.project.backend.services;

import com.project.backend.dtos.ResetPasswordDTO;
import com.project.backend.entities.User;
import com.project.backend.repositories.UserRepo;
import com.project.backend.validators.RegisterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class ResetPasswordService {
    private final UserRepo userRepo;
    private final Properties props;
    private final String USERNAME_AUTH = "99002cd62eadab";
    private final String PASSWORD_AUTH = "12f395fdbb9c9a";
    private final String ADMIN_EMAIL = "tennis@court.com";
    private final String SUBJECT = "Reset password";
    private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final SecureRandom rnd;
    private final RegisterValidator registerValidator;
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public ResetPasswordService(UserRepo userRepo, RegisterValidator registerValidator) {
        this.userRepo = userRepo;
        this.registerValidator = registerValidator;
        this.props = new Properties();
        this.rnd = new SecureRandom();
        configureProps();
    }

    private void configureProps(){
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", "smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");
    }

    public String sendEmail(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if(userOptional.isPresent()) {
            String code = generateRandomString(rnd.nextInt(AB.length()));
            String message = "Hello, " + userOptional.get().getUsername() + "! \n \nA request has been received to change " +
                    "the password for your account." + "\nUse this code to reset your password: " + code + "\n \nIf you did not " +
                    "request a password reset, you can safely ignore this email.";
            sendMail(email, message);

            return code;
        }

        return "Email was not found in the database!";
    }

    private void sendMail(String to, String content) {
        javax.mail.Session session = javax.mail.Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(USERNAME_AUTH, PASSWORD_AUTH);
                    }
                });

        try {
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ADMIN_EMAIL));
            message.setRecipients(MimeMessage.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(SUBJECT);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateRandomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public String resetPassword(String email, ResetPasswordDTO resetPasswordDTO) {
        if(resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmationPassword())) {
            registerValidator.validatePassword(resetPasswordDTO.getPassword());

            Optional<User> userOptional = userRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(bCryptPasswordEncoder.encode(resetPasswordDTO.getPassword()));
                userRepo.save(user);
                return "Ok";
            }else{
                return "User was not found in the db!";
            }
        }
        return "Password confirmation does not match password!";
    }
}
