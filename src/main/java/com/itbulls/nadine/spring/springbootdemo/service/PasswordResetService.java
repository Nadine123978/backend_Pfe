package com.itbulls.nadine.spring.springbootdemo.service;

import java.time.LocalDateTime;

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
}
