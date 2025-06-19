package com.itbulls.nadine.spring.springbootdemo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventStatusScheduler {

    @Autowired
    private EventRepository eventRepository;

    @Scheduled(fixedRate = 6000) // كل دقيقة
    public void updateEventStatuses() {
        List<Event> events = eventRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Event event : events) {
            if (event.getStartDate() != null && event.getEndDate() != null) {
                String newStatus;
                if (event.getStartDate().isAfter(now)) {
                    newStatus = "upcoming";
                } else if (event.getEndDate().isBefore(now)) {
                    newStatus = "past";
                } else {
                    newStatus = "active";
                }

                if (!newStatus.equals(event.getStatus())) {
                    event.setStatus(newStatus);
                    eventRepository.save(event);
                }
            }
        }
    }
}
