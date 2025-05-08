package com.itbulls.nadine.spring.springbootdemo.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itbulls.nadine.spring.springbootdemo.model.PasswordResetToken;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.PasswordResetTokenRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private EmailService emailService;

    public boolean validateToken(String token) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token);

        if (resetToken != null && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            return true; // التوكن صالح
        }
        return false; // التوكن غير صالح
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token);

        if (resetToken != null && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            User user = userRepo.findByEmail(resetToken.getEmail());
            user.setPassword(newPassword); // تأكد من تشفير كلمة المرور قبل حفظها
            userRepo.save(user);
            tokenRepo.delete(resetToken); // حذف التوكن بعد الاستخدام
        }
    }
    
    public boolean createAndSendResetToken(String email) {
        System.out.println("START createAndSendResetToken with email: " + email);  // ← هنا
        
        User user = userRepo.findByEmail(email);
        if (user == null) {
            System.out.println("User not found!");
            return false;
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);
        PasswordResetToken resetToken = new PasswordResetToken(token, email, expiry);

        tokenRepo.save(resetToken);  // ← لازم يوصل لهون
        System.out.println("Token saved: " + token); 
        String resetLink = "http://localhost:8081/auth/reset-password?token=" + token;
        emailService.sendResetEmail(email, resetLink);
// ← هنا
        
        System.out.println("Reset password link: http://localhost:8081/auth/reset-password?token=" + token);
        return true;
    }


}
