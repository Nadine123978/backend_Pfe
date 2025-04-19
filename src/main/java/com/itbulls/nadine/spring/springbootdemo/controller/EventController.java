package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Category;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Location;
import com.itbulls.nadine.spring.springbootdemo.repository.CategoryRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.LocationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        if (event.getCategory() == null || event.getLocation() == null) {
            return ResponseEntity.badRequest().body("Category and Location are required.");
        }

        Category category = categoryRepository.findById(event.getCategory().getId()).orElse(null);
        Location location = locationRepository.findById(event.getLocation().getId()).orElse(null);

        if (category == null || location == null) {
            return ResponseEntity.badRequest().body("Invalid category or location ID.");
        }

        event.setCategory(category);
        event.setLocation(location);
        Event savedEvent = eventRepository.save(event);

        return ResponseEntity.ok(savedEvent);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Event>> getEventsByStatus(@PathVariable String status) {
        List<Event> events = eventRepository.findByStatus(status);
        return ResponseEntity.ok(events);
    }

}
