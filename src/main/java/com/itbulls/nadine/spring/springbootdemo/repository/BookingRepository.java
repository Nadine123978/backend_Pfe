package com.itbulls.nadine.spring.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // فيك تضيف طرق مخصصة هون إذا بدك لاحقًا
}
