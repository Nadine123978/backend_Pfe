package com.itbulls.nadine.spring.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.itbulls.nadine.spring.springbootdemo.model.Event;



public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Add this method
	List<Event> findByStatusIgnoreCase(String status);
	  long countByStatus(String status);

}