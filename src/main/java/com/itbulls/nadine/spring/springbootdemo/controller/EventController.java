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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @PostMapping("/upload")
    public ResponseEntity<Event> createEvent(
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam double price,
        @RequestParam int soldTickets,
        @RequestParam int totalTickets,
        @RequestParam(required = false) String status, // اجعل status اختياري
        @RequestParam(required = false) String date,  // إذا لم يتم تحديد تاريخ
        @RequestParam Long categoryId,
        @RequestParam Long locationId,
        @RequestParam(required = false) MultipartFile image,
        @RequestParam String startDate,
        @RequestParam("file") MultipartFile file,
        @RequestParam(required = false) boolean isFeatured // إضافة isFeatured هنا
    ) throws IOException {

        // إذا ما كان status موجود، نحدده كـ "draft" افتراضيًا
        if (status == null || status.isEmpty()) {
            status = "draft";
        }

        // إذا لم يكن هناك تاريخ معين، نعينه كـ null
        LocalDateTime eventDate = null;
        if (date != null && !date.isEmpty()) {
            eventDate = LocalDateTime.parse(date); // تحويل string إلى LocalDateTime
        }

        // 2. إنشاء الحدث
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setPrice(price);
        event.setSoldTickets(soldTickets);
        event.setTotalTickets(totalTickets);
        event.setStatus(status); // تعيين status افتراضيًا "draft" إذا لم يتم تمريره
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        event.setStartDate(startDateTime);

        // تعيين قيمة isFeatured
        event.setIsFeatured(isFeatured);

        // إنشاء مجلد الصور إذا مش موجود
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // احفظ الصورة فعلياً
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes());

        // عيّن المسار الكامل للرابط يلي بيتعرض بالواجهة
        String imageUrl = "http://localhost:8081/uploads/" + fileName;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return eventRepository.findById(id).map(event -> {
            try {
                if (updates.containsKey("title")) {
                    event.setTitle((String) updates.get("title"));
                }

                if (updates.containsKey("description")) {
                    event.setDescription((String) updates.get("description"));
                }

                if (updates.containsKey("price")) {
                    event.setPrice(Double.parseDouble(updates.get("price").toString()));
                }

                if (updates.containsKey("status")) {
                    event.setStatus((String) updates.get("status"));
                }

                if (updates.containsKey("soldTickets")) {
                    event.setSoldTickets(Integer.parseInt(updates.get("soldTickets").toString()));
                }

                if (updates.containsKey("totalTickets")) {
                    event.setTotalTickets(Integer.parseInt(updates.get("totalTickets").toString()));
                }

                if (updates.containsKey("startDate")) {
                    event.setStartDate(LocalDateTime.parse((String) updates.get("startDate")));
                }

                if (updates.containsKey("endDate")) {
                    event.setEndDate(LocalDateTime.parse((String) updates.get("endDate")));
                }

                if (updates.containsKey("category")) {
                    Map<String, Object> categoryMap = (Map<String, Object>) updates.get("category");
                    Long categoryId = Long.parseLong(categoryMap.get("id").toString());
                    categoryRepository.findById(categoryId).ifPresent(event::setCategory);
                }

                if (updates.containsKey("location")) {
                    Map<String, Object> locationMap = (Map<String, Object>) updates.get("location");
                    Long locationId = Long.parseLong(locationMap.get("id").toString());
                    locationRepository.findById(locationId).ifPresent(event::setLocation);
                }

                if (updates.containsKey("isFeatured")) {
                    event.setIsFeatured(Boolean.parseBoolean(updates.get("isFeatured").toString())); // تعيين isFeatured
                }

                Event updated = eventRepository.save(event);
                return ResponseEntity.ok(updated);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error while updating event: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/featured")
    public List<Event> getFeaturedEvents() {
        return eventRepository.findByIsFeaturedTrue(); // إرجاع الأحداث المميزة فقط
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @CrossOrigin(origins = "http://localhost:5174")
    @GetMapping("/by-status")
    public ResponseEntity<List<Event>> getEventsByStatus(@RequestParam(required = false) List<String> status) {
        List<Event> events;
        if (status != null && !status.isEmpty()) {
            events = eventRepository.findByStatusInIgnoreCase(status); // استخدم findByStatusInIgnoreCase لجلب الحالات المتعددة
        } else {
            events = eventRepository.findAll();
        }
        return ResponseEntity.ok(events);
    }

    
    @GetMapping("/count")
    public Map<String, Long> getEventCounts() {
        Map<String, Long> eventCounts = new HashMap<>();
        eventCounts.put("active", eventRepository.countByStatus("active"));
        eventCounts.put("draft", eventRepository.countByStatus("draft"));
        eventCounts.put("past", eventRepository.countByStatus("past"));
        return eventCounts;
    }
    @PutMapping("/update-status/{id}")
    public ResponseEntity<Event> updateEventStatus(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElse(null);

        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        if (event.getStartDate().isAfter(LocalDateTime.now())) {
            event.setStatus("upcoming");
        } else if (event.getEndDate().isBefore(LocalDateTime.now())) {
            event.setStatus("past");
        } else {
            event.setStatus("active");
        }

        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }
    

}