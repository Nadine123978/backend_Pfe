package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	boolean existsBySeat(Seat seat);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByStatusAndExpiresAtBefore(String status, LocalDateTime now);

    
    @Modifying
    @Transactional
    @Query("DELETE FROM Booking b WHERE b.user.id = :userId")
    //void deleteByUserId(@Param("userId") Long userId);
    void deleteByUserId(Long userId);
    long count();
    long countByStatus(String status); 
}
