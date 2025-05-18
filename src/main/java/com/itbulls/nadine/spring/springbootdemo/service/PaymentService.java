package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Payment;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public Payment processPayment(Long bookingId, Double amount, String method) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        Booking booking = optionalBooking.get();

        if (!"HELD".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Cannot pay for booking that is not in HELD status");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setPaidAt(LocalDateTime.now());

        // Save the payment
        Payment savedPayment = paymentRepository.save(payment);

        // Update the booking status
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);

        return savedPayment;
    }
}
