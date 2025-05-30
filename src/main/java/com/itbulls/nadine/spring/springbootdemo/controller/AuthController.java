package com.itbulls.nadine.spring.springbootdemo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itbulls.nadine.spring.springbootdemo.dto.LoginRequest;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import com.itbulls.nadine.spring.springbootdemo.service.PasswordResetService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordResetService resetService;

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (resetService.validateToken(token)) {
            resetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password has been reset successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
    }
    
    @PostMapping("/request-reset-password")
    public ResponseEntity<String> requestResetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        boolean result = resetService.createAndSendResetToken(email);
        if (result) {
            return ResponseEntity.ok("Password reset link has been sent");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
    }
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
         
        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("userId", user.getId());
        String group = (user.getGroup() != null && user.getGroup().getName() != null) ? user.getGroup().getName() : "user";
        response.put("group", group);

        return ResponseEntity.ok(response);
    }
}

