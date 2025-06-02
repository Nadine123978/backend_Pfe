package com.itbulls.nadine.spring.springbootdemo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.EventImage;
import com.itbulls.nadine.spring.springbootdemo.repository.EventImageRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;

@Service
public class EventImageService {

    @Autowired
    private EventImageRepository eventImageRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<EventImage> getImagesByEventId(Long eventId) {
        return eventImageRepository.findByEventId(eventId);
    }

    public EventImage saveImage(Long eventId, String imageUrl) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        EventImage image = new EventImage();
        image.setImageUrl(imageUrl);
        image.setEvent(event); // ✅ ربط الصورة بالـ Event
        return eventImageRepository.save(image);
    }

    public void deleteImage(Long imageId) {
        eventImageRepository.deleteById(imageId);
    }
}
