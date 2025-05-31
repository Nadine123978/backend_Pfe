package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.EmailService;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('USER')")
public class BookingController {

    @Autowired
    @Lazy
    private BookingService bookingService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;


    // طلب تأكيد الحجز بعد الدفع (POST)
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

        // إرسال البريد الإلكتروني لتأكيد الحجز
        emailService.sendBookingConfirmation(
            booking.getUser().getEmail(),
            "تم تأكيد الحجز",
            "تم حجز المقعد " + booking.getSeat().getCode() + " بنجاح."
        );

        return ResponseEntity.ok("Booking confirmed successfully and ticket sent to your email.");
    }

    // إنشاء طلب حجز جديد باستخدام JSON body
    public static class BookingRequest {
        private Long userId;
        private Long eventId;

        // getters
        public Long getUserId() {
            return userId;
        }

        public Long getEventId() {
            return eventId;
        }

        // setters
        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            User user = userService.findByEmail(email);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }

            // التحقق إذا الـ userId الموجود في الطلب هو نفس الـ userId في Authentication
            if (!user.getId().equals(bookingRequest.getUserId())) {
                return ResponseEntity.status(403).body("You are not allowed to create bookings for other users");
            }

            Booking booking = bookingService.createBooking(user.getId(), bookingRequest.getEventId());
            return ResponseEntity.ok(booking);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        var bookings = bookingService.getBookingsForUser(user.getId());

        return ResponseEntity.ok(bookings);
    }

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

    // جلب تفاصيل حجز معين
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        Optional<Booking> optional = bookingService.getBookingById(bookingId);
        return optional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // جلب جميع الحجوزات لمستخدم معين (اختياري)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId));
    }
}
