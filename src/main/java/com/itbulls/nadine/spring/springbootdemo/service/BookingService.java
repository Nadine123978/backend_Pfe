package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;

import jakarta.transaction.Transactional;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

	    @Autowired
	    private EmailService emailService; // 👈

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatService seatService;

   
    public Booking holdSeat(Seat seat, User user, Double price) {
        seatService.markSeatAsReserved(seat);
        
        if (bookingRepository.existsBySeat(seat)) {
            throw new RuntimeException("This seat is already booked.");
        }

        Booking booking = new Booking();
        booking.setSeat(seat);
        booking.setUser(user);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // صلاحية 15 دقيقة
        booking.setStatus("HELD");
        booking.setPrice(price);
        
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        return bookingRepository.save(booking);
    }
   
 // تأكيد الحجز بعد الدفع مع إرسال الإيميل

    @PostMapping("/confirm")
    @Transactional
    public ResponseEntity<?> confirmBooking(
            @RequestParam Long bookingId,
            @RequestParam String paymentMethod
    ) {
    	Optional<Booking> optionalBooking = getBookingById(bookingId);
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.badRequest().body("Booking not found");
        }

        Booking booking = optionalBooking.get();
        booking.setConfirmed(true);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus("CONFIRMED");

        saveBooking(booking);

        // إرسال الإيميل
        String email = booking.getUser().getEmail();
        String seatCode = booking.getSeat().getCode();
        String subject = "Booking Confirmation";
        String body = "Your booking is confirmed. Seat code: " + seatCode;

        emailService.sendBookingConfirmation(email, subject, body);

        return ResponseEntity.ok("Booking confirmed and ticket sent to your email.");
    }


    public List<Booking> getExpiredHeldBookings(LocalDateTime now) {
        return bookingRepository.findByStatusAndExpiresAtBefore("HELD", now);
    }


    public void cancelBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("CANCELLED");
            seatService.markSeatAsAvailable(booking.getSeat());
            bookingRepository.save(booking);
        } else {
            throw new RuntimeException("Booking not found");
        }
    }

    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
    
    public void saveBooking(Booking booking) {
        bookingRepository.save(booking);
    }
    
    @Scheduled(fixedRate = 60000) // كل دقيقة
    public void releaseExpiredHolds() {
        List<Booking> expired = bookingRepository.findByStatusAndExpiresAtBefore("HELD", LocalDateTime.now());
        
        for (Booking booking : expired) {
            booking.setStatus("CANCELLED");
            seatService.markSeatAsAvailable(booking.getSeat());
            bookingRepository.save(booking);
        }
    }
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    public Booking createBooking(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus("PENDING");
        booking.setConfirmed(false);
        
        booking.setCreatedAt(LocalDateTime.now());
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        return bookingRepository.save(booking);
    }

}
