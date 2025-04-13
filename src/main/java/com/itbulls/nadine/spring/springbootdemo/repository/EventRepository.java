
package com.itbulls.nadine.spring.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.itbulls.nadine.spring.springbootdemo.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}