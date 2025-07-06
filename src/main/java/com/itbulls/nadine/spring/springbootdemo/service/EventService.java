package com.itbulls.nadine.spring.springbootdemo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itbulls.nadine.spring.springbootdemo.dto.EventWithBookingInfoDTO;
import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public List<Event> getEventsWithoutFolders() {
        return eventRepository.findEventsWithoutFolders();
    }
    
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    // دالة ترجع الأحداث القادمة (بديلة عن findUpcomingEvents في Repository)
    public List<Event> findUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findAll().stream()
                .filter(event -> event.getStartDate() != null && event.getStartDate().isAfter(now))
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .collect(Collectors.toList());
    }
    public List<Event> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findAll().stream()
                .filter(event -> event.getStartDate() != null && event.getStartDate().isAfter(now))
                .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate()))
                .collect(Collectors.toList());
    }


    // الدالة التي تُرجع الأحداث مع معلومات الحجز حسب المستخدم الحالي
    public List<EventWithBookingInfoDTO> getUpcomingEventsWithBookingInfo(Long currentUserId) {
        List<Event> events = eventRepository.findUpcomingEvents();

        List<EventWithBookingInfoDTO> dtoList = new ArrayList<>();

        for (Event event : events) {
            EventWithBookingInfoDTO dto = new EventWithBookingInfoDTO();
            dto.setId(event.getId());
            dto.setTitle(event.getTitle());
            dto.setImageUrl(event.getImageUrl());
            dto.setStartDate(event.getStartDate());
            dto.setEndDate(event.getEndDate());

            // نجيب آخر حجز حسب createdAt لكل event و user
            Optional<Booking> lastBookingOpt = event.getBookings().stream()
                .filter(b -> b.getUser().getId().equals(currentUserId))
                .max(Comparator.comparing(Booking::getCreatedAt));  // أحدث حجز

            if (lastBookingOpt.isPresent()) {
                Booking booking = lastBookingOpt.get();
                dto.setAlreadyBooked(true);
                dto.setBookingStatus(booking.getStatus().name());
                dto.setBookingId(booking.getId());

                boolean expired = booking.getExpiresAt() != null && booking.getExpiresAt().isBefore(LocalDateTime.now());
                dto.setBookingExpired(expired);
            } else {
                dto.setAlreadyBooked(false);
                dto.setBookingStatus(null);
                dto.setBookingExpired(false);
                dto.setBookingId(null);
            }

            dtoList.add(dto);
        }

        return dtoList;
    }
    public List<Event> getUpcomingPublishedEvents() {
        return eventRepository.findUpcomingPublishedEvents();
    }

}
