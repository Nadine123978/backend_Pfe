package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.BookingStatus;
import com.itbulls.nadine.spring.springbootdemo.model.Payment;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
boolean existsBySeatsContaining(Seat seat);

    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime dateTime);

    boolean existsByUserIdAndEventIdAndStatus(Long userId, Long eventId, BookingStatus status);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    
    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.confirmed = false AND b.expiresAt < :now")
    List<Booking> findExpiredUnconfirmedUnpaidBookings(@Param("status") BookingStatus status, @Param("now") LocalDateTime now);


    @Modifying
    @Transactional
    @Query("DELETE FROM Booking b WHERE b.user.id = :userId")
    //void deleteByUserId(@Param("userId") Long userId);
    void deleteByUserId(Long userId);
    long count();
    long countByStatus(BookingStatus status);
    List<Booking> findByUserIdAndEventId(Long userId, Long eventId);
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.user LEFT JOIN FETCH b.event LEFT JOIN FETCH b.seats")
    List<Booking> findAllWithDetails();
    @Query("SELECT b FROM Booking b JOIN FETCH b.user WHERE b.id = :id")
    Optional<Booking> findByIdWithUser(@Param("id") Long id);
    


}
