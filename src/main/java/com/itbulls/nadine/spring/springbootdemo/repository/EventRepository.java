
package com.itbulls.nadine.spring.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.itbulls.nadine.spring.springbootdemo.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status); // للفلترة مثلاً
}
