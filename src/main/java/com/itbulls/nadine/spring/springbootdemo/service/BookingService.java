package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

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
        booking.setStatus("HELD");
        booking.setPrice(price);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus("CONFIRMED");
            booking.setConfirmed(true);

            // 🟣 أهم شي هون: تحديث حالة المقعد
            Seat seat = booking.getSeat();
            seat.setReserved(true);
            seatService.saveSeat(seat); // لازم تكون موجودة بـ SeatService

            return bookingRepository.save(booking);
        }
        throw new RuntimeException("Booking not found");
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

}
