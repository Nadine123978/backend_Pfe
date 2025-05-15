package com.itbulls.nadine.spring.springbootdemo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itbulls.nadine.spring.springbootdemo.model.TicketSection;

import java.util.List;

public interface TicketSectionRepository extends JpaRepository<TicketSection, Long> {
    List<TicketSection> findByEventId(Long eventId);
}
