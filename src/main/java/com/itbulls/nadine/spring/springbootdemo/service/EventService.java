package com.itbulls.nadine.spring.springbootdemo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // بدّك تنفّذ logic بيرجع events يلي ما عندن folders
        return eventRepository.findEventsWithoutFolders();
    }
}

