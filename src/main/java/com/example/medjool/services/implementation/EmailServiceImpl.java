package com.example.medjool.services.implementation;

import com.example.medjool.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail() {
        String subject = "Password Reset Request";
        String message = "Click the link to reset your password: ";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("salaheddine.samid@medjoolstar.com"); // e.g., user@outlook.com
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("salaheddine.kobra@iu-study.org");


        mailSender.send(mailMessage);
    }
}
