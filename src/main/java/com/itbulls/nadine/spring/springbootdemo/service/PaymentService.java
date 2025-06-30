package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Payment;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public void handlePayment(String paymentMethod, String fullName, String phoneNumber,
            String receiptNumber, Long bookingId, Double amount,
            MultipartFile receiptImage) throws IOException {

Booking booking = bookingRepository.findById(bookingId)
.orElseThrow(() -> new RuntimeException("Booking not found for id: " + bookingId));

Payment payment = new Payment();
payment.setBooking(booking);
payment.setPaymentMethod(paymentMethod);
payment.setAmount(amount);
payment.setPaidAt(LocalDateTime.now());

// يمكنك حذف هذا اذا لم يعد لديك orderNumber
// payment.setOrderNumber(orderNumber);

if (isOffline(paymentMethod)) {
payment.setFullName(fullName);
payment.setPhoneNumber(phoneNumber);
payment.setReceiptNumber(receiptNumber);
payment.setStatus("PAID");

if (receiptImage != null && !receiptImage.isEmpty()) {
String path = saveReceiptImage(receiptImage);
payment.setReceiptImagePath(path);
}
} else {
payment.setStatus("CONFIRMED");
}

paymentRepository.save(payment);
}

    private boolean isOffline(String method) {
        return List.of("OMT", "CashUnited", "MyMonty", "Malik", "Libanpost").contains(method);
    }

    private String saveReceiptImage(MultipartFile image) throws IOException {
        String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path path = Paths.get("uploads/receipts/" + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        return path.toString();
    }
}
