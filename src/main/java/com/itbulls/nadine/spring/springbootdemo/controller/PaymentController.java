package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Payment;
import com.itbulls.nadine.spring.springbootdemo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> processPayment(
            @RequestParam Long bookingId,
            @RequestParam Double amount,
            @RequestParam String method
    ) {
        try {
            Payment payment = paymentService.processPayment(bookingId, amount, method);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
