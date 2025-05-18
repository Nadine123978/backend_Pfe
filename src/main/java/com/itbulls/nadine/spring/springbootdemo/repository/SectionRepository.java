package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByEventId(Long eventId);
}
