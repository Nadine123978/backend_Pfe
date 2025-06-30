package com.itbulls.nadine.spring.springbootdemo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itbulls.nadine.spring.springbootdemo.service.EmailService;
import com.itbulls.nadine.spring.springbootdemo.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;  // ضيف خدمة الإيميل

    @PostMapping(value = "/pay", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> processPayment(
        @RequestParam("paymentMethod") String paymentMethod,
        @RequestParam(value = "fullName", required = false) String fullName,
        @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
        @RequestParam(value = "receiptNumber", required = false) String receiptNumber,
        @RequestParam("bookingId") String bookingIdStr,
        @RequestParam("amount") String amountStr,
        @RequestParam(value = "email", required = false) String email,
 

        @RequestPart(value = "receiptImage", required = false) MultipartFile receiptImage
    ) {
        try {
            Long bookingId = Long.parseLong(bookingIdStr);
            Double amount = Double.parseDouble(amountStr);
            System.out.println("Email received: " + email);

            // 1- معالجة الدفع (الحفظ في قاعدة البيانات مثلاً)
            paymentService.handlePayment(paymentMethod, fullName, phoneNumber, receiptNumber, bookingId, amount, receiptImage);

            // 2- إرسال إيميل تأكيد الدفع
            emailService.sendPaymentConfirmationEmail(email, fullName != null ? fullName : "Customer", "ORD123456"); 
            // بدل ORD123456 استخدم رقم الطلب الحقيقي من قاعدة البيانات لو عندك

            return ResponseEntity.ok("Payment processed successfully.");
        } catch (NumberFormatException nfe) {
            return ResponseEntity.badRequest().body("Invalid number format.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("PaymentController is working");
    }
}
