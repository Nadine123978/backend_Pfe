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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.nio.file.Files;

import java.util.List;
import java.util.UUID;
@CrossOrigin(origins = "http://localhost:5174")
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
    
    @PostMapping("/upload")
    public ResponseEntity<Event> createEvent(
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam double price,
        @RequestParam int soldTickets,
        @RequestParam int totalTickets,
        @RequestParam String status,
        @RequestParam String date,  // حطيت String لأنه لازم يصير تحويل
        @RequestParam Long categoryId,
        @RequestParam Long locationId,
        @RequestParam(required = false) MultipartFile image
    ) throws IOException {
    	
    	Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);  // إنشاء المجلد إذا لم يكن موجودًا
        }
        
        // 1. حفظ الصورة في السيرفر أو cloud service
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName); // مسار مجلد الحفظ
            Files.write(path, image.getBytes());
            imageUrl = "/uploads/" + fileName;
        }

        // 2. تحويل التاريخ من String إلى LocalDateTime
        LocalDateTime eventDate = LocalDateTime.parse(date); // تحويل string إلى LocalDateTime

        // 3. إنشاء الحدث
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setPrice(price);
        event.setSoldTickets(soldTickets);
        event.setTotalTickets(totalTickets);
        event.setStatus(status);
        event.setDate(eventDate); // تمرير التاريخ كـ LocalDateTime
        event.setImageUrl(imageUrl);
        
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Location location = locationRepository.findById(locationId).orElse(null);

        if (category != null && location != null) {
            event.setCategory(category);
            event.setLocation(location);
        } else {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(eventRepository.save(event));
    }


    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(@RequestParam(required = false) String status) {
        List<Event> events;

        if (status != null) {
            events = eventRepository.findByStatusIgnoreCase(status);
        } else {
            events = eventRepository.findAll();
        }

        return ResponseEntity.ok(events);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam("title") String title) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCase(title);
        return ResponseEntity.ok(events);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        // تحقق مما إذا كان الحدث موجودًا في قاعدة البيانات
        Event event = eventRepository.findById(id).orElse(null);
        if (event != null) {
            // إذا كان الحدث موجودًا، احذفه
            eventRepository.delete(event);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }



}
