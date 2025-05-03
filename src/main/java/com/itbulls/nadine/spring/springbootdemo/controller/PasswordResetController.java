package com.itbulls.nadine.spring.springbootdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.itbulls.nadine.spring.springbootdemo.dto.PasswordResetRequest;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import com.itbulls.nadine.spring.springbootdemo.service.EmailService;

import org.springframework.http.ResponseEntity;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173/", "http://localhost:5174/"})
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;  // افترض أنك تستخدم JPA لإدارة المستخدمين.

    @Autowired
    private EmailService emailService;

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        // تحقق مما إذا كان المستخدم موجودًا باستخدام البريد الإلكتروني
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(400).body("Email not found");
        }

        User user = userOpt.get();
        
        // إنشاء توكن فريد
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiration(System.currentTimeMillis() + 3600000);  // التوكن صالح لمدة ساعة
        userRepository.save(user);

        // إرسال رابط إعادة تعيين كلمة المرور
        String resetLink = "http://localhost:5174/reset-password/" + resetToken;
        emailService.sendPasswordResetEmail(request.getEmail(), resetLink);

        return ResponseEntity.ok("Password reset link sent to your email.");
    }
}

