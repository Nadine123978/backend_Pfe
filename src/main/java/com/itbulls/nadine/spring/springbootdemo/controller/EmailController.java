package com.itbulls.nadine.spring.springbootdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbulls.nadine.spring.springbootdemo.dto.BookingEmailRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.ReplyRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.TicketInfo;
import com.itbulls.nadine.spring.springbootdemo.model.Email;
import com.itbulls.nadine.spring.springbootdemo.repository.EmailRepository;
import com.itbulls.nadine.spring.springbootdemo.service.EmailService;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "http://localhost:5173")  // اسم موقع React frontend
public class EmailController {

    @Autowired
    private EmailService emailService;

    private final EmailRepository emailRepository;

    public EmailController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @GetMapping
    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }
    
    @PostMapping("/save-emails")
    public ResponseEntity<?> saveEmailsAndNotify(@RequestBody BookingEmailRequest request) {
        for (TicketInfo ticket : request.getTickets()) {
            // Save user info to DB (if needed)
            
            // Send email:
            emailService.sendEmail(
                ticket.getEmail(),
                "Ticket Reserved Temporarily",
                "Hi " + ticket.getFirstName() + ",\n\n" +
                "You have reserved a ticket. Please complete the payment within 10 minutes or your reservation will be cancelled.\n\n" +
                "Booking ID: " + request.getBookingId()
            );
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping("/send-reply")
    public ResponseEntity<?> sendReplyEmail(@RequestBody ReplyRequest request) {
        try {
            emailService.sendEmail(
                request.getToEmail(),
                request.getSubject(),
                request.getMessage()
            );
            return ResponseEntity.ok("Reply email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }
}
