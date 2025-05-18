package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	boolean existsBySeat(Seat seat);
    List<Booking> findByUserId(Long userId);
}
