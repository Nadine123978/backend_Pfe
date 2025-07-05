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

    @Scheduled(fixedRate = 60000)
    public void updateEventStatuses() {
        List<Event> events = eventRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Event event : events) {
            if (event.getStatus() == EventStatus.DRAFT
                || event.getStatus() == EventStatus.CANCELLED
                || event.getStatus() == EventStatus.ARCHIVED) {
                // لا تغير حالة المسودات أو الملغية أو المؤرشفة
                continue;
            }

            if (event.getStartDate() != null && event.getEndDate() != null) {
                EventStatus newStatus;
                if (event.getStartDate().isAfter(now)) {
                    newStatus = EventStatus.UPCOMING;
                } else if (event.getEndDate().isBefore(now)) {
                    newStatus = EventStatus.PAST;
                } else {
                    newStatus = EventStatus.ACTIVE;
                }

                if (newStatus != event.getStatus()) {
                    event.setStatus(newStatus);
                    eventRepository.save(event);
                }
            }
        }
    }
}