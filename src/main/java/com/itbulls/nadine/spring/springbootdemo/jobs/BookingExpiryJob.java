package com.itbulls.nadine.spring.springbootdemo.jobs;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.BookingStatus;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingExpiryJob {

    @Autowired
    private BookingRepository bookingRepository; // ✅ أضفناها

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatService seatService;

    @Scheduled(fixedRate = 60000) // كل دقيقة
    public void cancelExpiredBookings() {
        List<Booking> bookings = bookingRepository.findExpiredUnconfirmedUnpaidBookings(
            BookingStatus.UNPAID,
            LocalDateTime.now()
        );

        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setConfirmed(false);
            bookingRepository.save(booking);
            System.out.println("Cancelled booking with ID: " + booking.getId());
        }
    }


}
