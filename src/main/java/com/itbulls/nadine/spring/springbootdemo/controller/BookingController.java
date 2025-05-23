package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.EmailService;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    @Lazy
    private BookingService bookingService;

    // تأكيد الحجز بعد الدفع باستخدام PUT
    //@PutMapping("/confirm/{bookingId}")
   // public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId) {
     //   try {
        //    Booking confirmedBooking = bookingService.confirmBooking(bookingId);
           // return ResponseEntity.ok(confirmedBooking);
      //  } catch (RuntimeException e) {
        //    return ResponseEntity.badRequest().body(e.getMessage());
     //   }
   // }

    // إلغاء الحجز واسترجاع المقعد
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // جلب تفاصيل حجز معيّن
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        Optional<Booking> optional = bookingService.getBookingById(bookingId);
        return optional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // (اختياري) جلب جميع الحجوزات الخاصة بمستخدم
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId));
    }

    // تأكيد الحجز بعد الدفع مع تحديد وسيلة الدفع (POST)
    @Autowired
    private EmailService emailService;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(
            @RequestParam Long bookingId,
            @RequestParam String paymentMethod
    ) {
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.badRequest().body("Booking not found");
        }

        Booking booking = optionalBooking.get();
        booking.setConfirmed(true);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus("CONFIRMED");

        bookingService.saveBooking(booking);

        // ✅ إرسال الإيميل
        emailService.sendBookingConfirmation(
            booking.getUser().getEmail(),
            "تم تأكيد الحجز",
            "تم حجز المقعد " + booking.getSeat().getCode() + " بنجاح."
        );

        return ResponseEntity.ok("Booking confirmed successfully and ticket sent to your email.");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestParam Long userId, @RequestParam Long eventId) {
        try {
            Booking booking = bookingService.createBooking(userId, eventId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
