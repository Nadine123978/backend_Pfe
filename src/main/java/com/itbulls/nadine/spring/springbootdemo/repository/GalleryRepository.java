package com.itbulls.nadine.spring.springbootdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itbulls.nadine.spring.springbootdemo.model.Gallery;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    List<Gallery> findByEventId(Long eventId);
}
