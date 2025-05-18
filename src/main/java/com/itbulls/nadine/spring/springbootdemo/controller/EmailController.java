package com.itbulls.nadine.spring.springbootdemo.controller;

import org.springframework.web.bind.annotation.*;

import com.itbulls.nadine.spring.springbootdemo.model.Email;
import com.itbulls.nadine.spring.springbootdemo.repository.EmailRepository;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "http://localhost:5174")  // اسم موقع React frontend
public class EmailController {

    private final EmailRepository emailRepository;

    public EmailController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @GetMapping
    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }
}
