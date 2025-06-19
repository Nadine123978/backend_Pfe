package com.itbulls.nadine.spring.springbootdemo.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.BookingStatus;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;

import jakarta.transaction.Transactional;

@Component
public class BookingCleanupScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000)  
    @Transactional
    public void cleanupExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        // جلب الحجوزات المنتهية ولم تؤكد بعد
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING, now);

        for (Booking booking : expiredBookings) {
            // فك قفل المقاعد المرتبطة
            List<Seat> seats = booking.getSeats();
            for (Seat seat : seats) {
                seat.setLocked(false);
                seat.setLockedUntil(null);
                seat.setReserved(false);
                seat.setBooking(null);
                seatRepository.save(seat);
            }

            // إلغاء الحجز (تغيير الحالة أو حذف حسب المطلوب)
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }
    }
}
