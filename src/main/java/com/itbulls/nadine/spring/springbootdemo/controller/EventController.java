package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.controller.BookingController.BookingRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.CheckAvailabilityRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.EventWithBookingInfoDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.SeatDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.SeatRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.TicketSectionDTO;
import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Category;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Location;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.CategoryRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.LocationRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.EventService;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;
import com.itbulls.nadine.spring.springbootdemo.repository.SectionRepository;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private final SeatRepository seatRepository;
    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;


    @Autowired
    public EventController(EventRepository eventRepository, SeatRepository seatRepository, EventService eventService) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // ✅ محمية: فقط admin أو superadmin
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<Event> createEvent(
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam(required = false) String status,
        @RequestParam Long categoryId,
        @RequestParam Long locationId,
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam("file") MultipartFile file,
        @RequestParam(required = false, defaultValue = "false") boolean isFeatured
    ) throws IOException {

        if (status == null || status.isEmpty()) status = "draft";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setStatus(status);
        event.setStartDate(startDateTime);
        event.setEndDate(endDateTime);
        event.setIsFeatured(isFeatured);

        // رفع الملف وحفظه في مجلد uploads
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes());

        event.setImageUrl("http://localhost:8081/uploads/" + fileName);

        Category category = categoryRepository.findById(categoryId).orElse(null);
        Location location = locationRepository.findById(locationId).orElse(null);

        if (category == null || location == null) {
            return ResponseEntity.badRequest().body(null);
        }

        event.setCategory(category);
        event.setLocation(location);

        // إضافة حفظ event في قاعدة البيانات وإرجاعه
        return ResponseEntity.ok(eventRepository.save(event));
    }


    // ✅ محمية: فقط admin أو superadmin
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return eventRepository.findById(id).map(event -> {
            try {
                if (updates.containsKey("title")) event.setTitle((String) updates.get("title"));
                if (updates.containsKey("description")) event.setDescription((String) updates.get("description"));
                if (updates.containsKey("status")) event.setStatus((String) updates.get("status"));
                if (updates.containsKey("startDate")) event.setStartDate(LocalDateTime.parse((String) updates.get("startDate")));
                if (updates.containsKey("endDate")) event.setEndDate(LocalDateTime.parse((String) updates.get("endDate")));
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
                if (updates.containsKey("isFeatured")) event.setIsFeatured(Boolean.parseBoolean(updates.get("isFeatured").toString()));

                Event updated = eventRepository.save(event);
                return ResponseEntity.ok(updated);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error while updating event: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<EventWithBookingInfoDTO>> getFeaturedEvents(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        User user = userService.findByEmail(email);

        List<Event> events = eventRepository.findByIsFeaturedTrue();

        List<EventWithBookingInfoDTO> result = events.stream().map(event -> {
            Optional<Booking> bookingOpt = bookingService.getBookingByUserIdAndEventId(user.getId(), event.getId());

            EventWithBookingInfoDTO dto = new EventWithBookingInfoDTO();
            dto.setId(event.getId());
            dto.setTitle(event.getTitle());
            dto.setImageUrl(event.getImageUrl());
            dto.setStartDate(event.getStartDate());
            dto.setEndDate(event.getEndDate());

            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                dto.setAlreadyBooked(true);
                dto.setBookingStatus(booking.getStatus().name());
                dto.setBookingExpired(booking.getExpiresAt().isBefore(LocalDateTime.now()));
                dto.setBookingId(booking.getId());  // ← أضف هالسطر
            } else {
                dto.setAlreadyBooked(false);
                dto.setBookingStatus(null);
                dto.setBookingExpired(false);
                dto.setBookingId(null);  // اختياري
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{eventId}/sections")
    public ResponseEntity<List<Section>> getSectionsByEventId(@PathVariable Long eventId) {
        List<Section> sections = sectionRepository.findByEventId(eventId);
        return sections.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(sections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            Optional<Event> event = eventRepository.findById(id);
            if (event.isPresent()) {
                return ResponseEntity.ok(event.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace(); // سيطبع الخطأ في Console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error occurred: " + e.getMessage());
        }
    }



    @CrossOrigin(origins = "http://localhost:5174")
    @GetMapping("/by-status")
    public ResponseEntity<List<Event>> getEventsByStatus(@RequestParam(required = false) List<String> status) {
        List<Event> events = (status != null && !status.isEmpty())
                ? eventRepository.findByStatusInIgnoreCase(status)
                : eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Event>> getEventsByCategory(@PathVariable Long categoryId) {
        List<Event> events = eventRepository.findByCategory_Id(categoryId);
        return events.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(events);
    }

    @GetMapping("/count")
    public Map<String, Long> getEventCounts() {
        Map<String, Long> eventCounts = new HashMap<>();
        eventCounts.put("active", eventRepository.countByStatus("active"));
        eventCounts.put("draft", eventRepository.countByStatus("draft"));
        eventCounts.put("past", eventRepository.countByStatus("past"));
        return eventCounts;
    }

    // ✅ محمية: فقط admin أو superadmin
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/update-status/{id}")
    public ResponseEntity<Event> updateEventStatus(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) return ResponseEntity.notFound().build();

        if (event.getStartDate().isAfter(LocalDateTime.now())) event.setStatus("upcoming");
        else if (event.getEndDate().isBefore(LocalDateTime.now())) event.setStatus("past");
        else event.setStatus("active");

        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/no-folders")
    public List<Event> getEventsWithoutFolders() {
        return eventService.getEventsWithoutFolders();
    }
    @PostMapping("/{id}/check-availability")
    public ResponseEntity<?> checkAvailability(@PathVariable Long id, @RequestBody CheckAvailabilityRequest request) {

        int totalRequested = request.getTotalRequestedSeats();

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        System.out.println("Event ID: " + id);
        System.out.println("Requested seats: " + totalRequested);
        System.out.println("Available seats before booking: " + event.getAvailableSeats());

        boolean available = totalRequested <= event.getAvailableSeats();

        int remainingSeats = event.getAvailableSeats() - totalRequested;
        if (remainingSeats < 0) remainingSeats = 0;

        System.out.println("Available after check: " + available);
        System.out.println("Remaining seats after booking: " + remainingSeats);

        return ResponseEntity.ok(Map.of(
            "available", available,
            "requestedSeats", totalRequested,
            "remainingSeats", remainingSeats
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
