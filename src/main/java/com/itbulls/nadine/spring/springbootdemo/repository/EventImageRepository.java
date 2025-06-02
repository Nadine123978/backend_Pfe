package com.itbulls.nadine.spring.springbootdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itbulls.nadine.spring.springbootdemo.model.EventImage;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
    List<EventImage> findByEventId(Long eventId);
}
