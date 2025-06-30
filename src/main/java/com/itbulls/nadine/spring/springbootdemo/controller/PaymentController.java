package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/pay", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> processPayment(
        @RequestParam("paymentMethod") String paymentMethod,
        @RequestParam(value = "fullName", required = false) String fullName,
        @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
        @RequestParam(value = "receiptNumber", required = false) String receiptNumber,
        @RequestParam("orderNumber") String orderNumber,
        @RequestParam("amount") String amountStr,
        @RequestPart(value = "receiptImage", required = false) MultipartFile receiptImage
    ) {
        try {
            // تحويل String إلى Double
            Double amount = Double.parseDouble(amountStr);

            paymentService.handlePayment(paymentMethod, fullName, phoneNumber, receiptNumber, orderNumber, amount, receiptImage);
            return ResponseEntity.ok("Payment processed successfully.");
        } catch (NumberFormatException nfe) {
            return ResponseEntity.badRequest().body("Invalid amount format.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
