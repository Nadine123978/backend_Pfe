package com.itbulls.nadine.spring.springbootdemo.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.itbulls.nadine.spring.springbootdemo.model.TicketType;


public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    // تعريفات إضافية إن وجدت
}
