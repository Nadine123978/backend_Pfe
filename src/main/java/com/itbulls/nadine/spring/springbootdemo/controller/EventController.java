package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.CheckAvailabilityRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.EventWithBookingInfoDTO;
import com.itbulls.nadine.spring.springbootdemo.model.*;
import com.itbulls.nadine.spring.springbootdemo.repository.*;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.EventService;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<Event> createEvent(
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam(required = false) String status,
        @RequestParam Long categoryId,
        @RequestParam Long locationId,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (status == null || status.isEmpty()) {
            status = "draft";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDateTime = (startDate != null && !startDate.isEmpty()) ? LocalDateTime.parse(startDate, formatter) : null;
        LocalDateTime endDateTime = (endDate != null && !endDate.isEmpty()) ? LocalDateTime.parse(endDate, formatter) : null;

        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setStatus(status);
        event.setStartDate(startDateTime);
        event.setEndDate(endDateTime);

        // Save image
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

        return ResponseEntity.ok(eventRepository.save(event));
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<Event> updateStatus(@PathVariable Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (!eventOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventOpt.get();

        LocalDateTime now = LocalDateTime.now();
        if (event.getStartDate() != null && event.getEndDate() != null) {
            if (event.getStartDate().isAfter(now)) {
                event.setStatus("upcoming");
            } else if (event.getEndDate().isBefore(now)) {
                event.setStatus("past");
            } else {
                event.setStatus("active");
            }
        } else {
            event.setStatus("draft");
        }

        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> upcomingEvents = eventRepository.findByStatusIgnoreCase("upcoming");
        return ResponseEntity.ok(upcomingEvents);
    }

    @GetMapping("/upcoming-with-booking")
    public ResponseEntity<?> getUpcomingEventsWithBooking(@RequestParam Long userId) {
        List<Event> upcomingEvents = eventService.getUpcomingEvents();
        List<Booking> userBookings = bookingService.getBookingsForUser(userId);

        List<EventWithBookingInfoDTO> result = upcomingEvents.stream().map(event -> {
            // كل الحجوزات للمستخدم على هذا الحدث
            List<Booking> bookingsForEvent = userBookings.stream()
                .filter(b -> b.getEvent().getId().equals(event.getId()))
                .collect(Collectors.toList());

            // نبحث أولاً عن آخر حجز غير ملغي
            Optional<Booking> lastActiveBookingOpt = bookingsForEvent.stream()
                .filter(b -> !b.getStatus().name().equals("CANCELLED"))
                .max(Comparator.comparing(Booking::getCreatedAt));

            // إذا ما وجدنا حجز غير ملغي، نأخذ آخر حجز مهما كانت الحالة
            Optional<Booking> lastBookingOpt = lastActiveBookingOpt.isPresent()
                ? lastActiveBookingOpt
                : bookingsForEvent.stream()
                    .max(Comparator.comparing(Booking::getCreatedAt));

            EventWithBookingInfoDTO dto = new EventWithBookingInfoDTO(event);

            if (lastBookingOpt.isPresent()) {
                Booking b = lastBookingOpt.get();
                dto.setAlreadyBooked(true);
                dto.setBookingStatus(b.getStatus().name());
                dto.setBookingId(b.getId());
            } else {
                dto.setAlreadyBooked(false);
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

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

                Event updated = eventRepository.save(event);
                return ResponseEntity.ok(updated);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error while updating event: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishEvent(@PathVariable Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (!eventOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventOpt.get();
        if (!"draft".equals(event.getStatus())) {
            return ResponseEntity.badRequest().body("Only draft events can be published");
        }

        // تحقق من البيانات المطلوبة
        if (event.getTitle() == null || event.getTitle().isEmpty()
            || event.getStartDate() == null
            || event.getEndDate() == null
            || event.getStartDate().isAfter(event.getEndDate())
            || event.getCategory() == null
            || event.getLocation() == null
            || event.getImageUrl() == null || event.getImageUrl().isEmpty()) {

            return ResponseEntity.badRequest().body("Event is incomplete and cannot be published.");
        }

        event.setStatus("active");
        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }
    @GetMapping("/drafts/old")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Event>> getOldDraftEvents() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Event> oldDrafts = eventRepository.findByStatusAndCreatedAtBefore("draft", sevenDaysAgo);
        return ResponseEntity.ok(oldDrafts);
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
            return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error occurred: " + e.getMessage());
        }
    }

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

    @GetMapping("/no-folders")
    public List<Event> getEventsWithoutFolders() {
        return eventService.getEventsWithoutFolders();
    }

    @PostMapping("/{id}/check-availability")
    public ResponseEntity<?> checkAvailability(@PathVariable Long id, @RequestBody CheckAvailabilityRequest request) {
        int totalRequested = request.getTotalRequestedSeats();

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        boolean available = totalRequested <= event.getAvailableSeats();
        int remainingSeats = Math.max(event.getAvailableSeats() - totalRequested, 0);

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
