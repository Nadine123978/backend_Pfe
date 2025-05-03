package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import com.itbulls.nadine.spring.springbootdemo.service.EmailService;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;  // خدمة البريد الإلكتروني لإرسال رابط إعادة تعيين كلمة المرور

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }

        User user = userOptional.get();

        // هنا يمكننا إرسال رابط البريد الإلكتروني مع رمز التحقق أو JWT
        String resetToken = generateResetToken(user); // يجب أن تنشئ هذه الدالة لتوليد رمز مميز

        // إرسال رابط إعادة تعيين كلمة المرور عبر البريد الإلكتروني
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    // دالة لتوليد رمز إعادة تعيين كلمة المرور
    private String generateResetToken(User user) {
        // توليد رمز مميز مع JWT أو باستخدام UUID
        return UUID.randomUUID().toString();
    }
}
