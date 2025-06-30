package com.itbulls.nadine.spring.springbootdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;




@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String resetLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Forgot your password?");

        String htmlContent = "<div style=\"font-family:Arial,sans-serif;font-size:16px;color:#333;line-height:1.6;\">"
                + "<h2>Forgot your password?</h2>"
                + "<p>Dear,</p>"
                + "<p>We received a request to change your password. Press the button below to create a new one.</p>"
                + "<a href=\"" + resetLink + "\" "
                + "style=\"display:inline-block;padding:12px 24px;background-color:#007bff;color:#ffffff;"
                + "text-decoration:none;border-radius:5px;margin-top:10px;\">Reset Password</a>"
                + "<p style=\"margin-top:20px;\">Didn't request this change? Ignore this email if you didn’t request to change your password.</p>"
                + "<p>Need help? Visit the <a href=\"http://yourwebsite.com/help\" style=\"color:#007bff;\">Help center</a> or contact us.</p>"
                + "</div>";

        helper.setText(htmlContent, true);  // true = HTML

        mailSender.send(message);
    }


    public void sendPaymentConfirmationEmail(String toEmail, String userName, String orderNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Payment Confirmation - Thank You!");
        message.setText("Dear " + userName + ",\n\n" +
                        "Thank you for your payment. Your order number is: " + orderNumber + ".\n" +
                        "We appreciate your trust.\n\n" +
                        "Best regards,\n" +
                        "Your Company Name");
        mailSender.send(message);
    }
    
    public void sendResetEmail(String email, String token) {
        // مثال بسيط
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String body = "Click the following link to reset your password:\n" + resetLink;

        // إذا كنت تستخدم JavaMailSender:
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    
    public void sendBookingConfirmation(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to); // ✅ استخدم اسم المتغير الصحيح
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendBookingConfirmationWithPDF(String toEmail, String subject, String body, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            helper.addAttachment("ticket.pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

}
