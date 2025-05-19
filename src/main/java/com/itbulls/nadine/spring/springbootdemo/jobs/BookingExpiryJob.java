package com.itbulls.nadine.spring.springbootdemo.jobs;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
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
    private BookingService bookingService;

    @Autowired
    private SeatService seatService;

    @Scheduled(fixedRate = 60000) // كل دقيقة
    public void cancelExpiredBookings() {
        List<Booking> expiredBookings = bookingService.getExpiredHeldBookings(LocalDateTime.now());
        for (Booking booking : expiredBookings) {
            booking.setStatus("CANCELLED");
            booking.setConfirmed(false);
            seatService.markSeatAsAvailable(booking.getSeat());
            bookingService.saveBooking(booking);
            System.out.println("Cancelled expired booking: " + booking.getId());
        }
    }
}
