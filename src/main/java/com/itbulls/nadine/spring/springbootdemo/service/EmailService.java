package com.itbulls.nadine.spring.springbootdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String to, String resetLink) {
    	System.out.println(">>> SENDING EMAIL TO: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Your Password");
        message.setText("Click this link to reset your password: " + resetLink);
        mailSender.send(message);
    }
}
