package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.BookingStatus;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;
    
    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
    
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking holdSeat(Seat seat, User user, Double price) {
        seatService.markSeatAsReserved(seat);

        if (bookingRepository.existsBySeatsContaining(seat)) {
            throw new RuntimeException("This seat is already booked.");
        }

        Booking booking = new Booking();
        booking.setSeats(List.of(seat));
        booking.setUser(user);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(15)); // صلاحية 15 دقيقة
        booking.setStatus(BookingStatus.HELD);  // استخدام enum
        booking.setPrice(price);

        return bookingRepository.save(booking);
    }

    @Transactional
    public ResponseEntity<?> confirmBooking(Long bookingId, String paymentMethod) {
        Optional<Booking> optionalBooking = getBookingById(bookingId);
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.badRequest().body("Booking not found");
        }

        Booking booking = optionalBooking.get();
        booking.setConfirmed(true);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus(BookingStatus.CONFIRMED);

        saveBooking(booking);

        // إرسال الإيميل
        String email = booking.getUser().getEmail();
        List<Seat> seats = booking.getSeats();

        String seatCodes = seats.stream()
                                .map(Seat::getCode)
                                .collect(Collectors.joining(", "));

        String subject = "Booking Confirmation";
        String body = "Your booking is confirmed. Seat codes: " + seatCodes;

        emailService.sendBookingConfirmation(email, subject, body);

        return ResponseEntity.ok("Booking confirmed and ticket sent to your email.");
    }

    public List<Booking> getExpiredHeldBookings(LocalDateTime now) {
        return bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.HELD, now);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        List<Seat> seats = booking.getSeats();
        if (seats != null) {
            for (Seat seat : seats) {
                seat.setReserved(false);
                seat.setLocked(false);
                seat.setLockedUntil(null);
                seat.setBooking(null);
                seatRepository.save(seat);
            }
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }



    public List<Booking> createBooking(Long userId, Long eventId, List<Long> seatIds) {
        if (hasConfirmedBooking(userId, eventId)) {
            throw new RuntimeException("You have already booked and confirmed this event.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<Seat> seatsToBook = new ArrayList<>();
        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            if (seat.isReserved()) {
                throw new RuntimeException("Seat with ID " + seatId + " is already reserved.");
            }

            seatsToBook.add(seat);
        }

        List<Booking> bookings = new ArrayList<>();
        for (Seat seat : seatsToBook) {
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setEvent(event);
            booking.setSeats(List.of(seat)); // لو حددت مقعد واحد
            booking.setStatus(BookingStatus.PENDING);
            booking.setConfirmed(false);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setExpiresAt(LocalDateTime.now().plusMinutes(15));

            bookingRepository.save(booking);
            bookings.add(booking);

            seat.setReserved(true);
            seatRepository.save(seat);
        }

        return bookings;
    }


    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public boolean hasConfirmedBooking(Long userId, Long eventId) {
        return bookingRepository.existsByUserIdAndEventIdAndStatus(userId, eventId, BookingStatus.CONFIRMED);
    }

    public boolean bookingExists(Long userId, Long eventId) {
        return bookingRepository.existsByUserIdAndEventId(userId, eventId);
    }

    public List<Booking> getBookingsByUserIdAndEventId(Long userId, Long eventId) {
        return bookingRepository.findByUserIdAndEventId(userId, eventId);
    }
    
    public Optional<Booking> getBookingByUserIdAndEventIdOptional(Long userId, Long eventId) {
       List<Booking> bookings = bookingRepository.findByUserIdAndEventId(userId, eventId);
       return bookings.stream().findFirst();
   }


 // BookingService.java
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public void confirmPayment(List<Booking> bookings) {
        for (Booking booking : bookings) {
            booking.setConfirmed(true);
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            // يمكنك إرسال إيميل تأكيد لكل حجز
            String email = booking.getUser().getEmail();
            List<String> seatCodes = booking.getSeats().stream()
            	    .map(Seat::getCode)
            	    .collect(Collectors.toList());

            String subject = "Booking Payment Confirmed";
            String body = "Your booking for seat " + seatCodes + " is confirmed and paid.";

            emailService.sendBookingConfirmation(email, subject, body);
        }
    }

  
}
