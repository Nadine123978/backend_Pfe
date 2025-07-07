package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.CategoryDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.CheckAvailabilityRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.EventDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.EventWithBookingInfoDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.LocationDTO;
import com.itbulls.nadine.spring.springbootdemo.model.*;
import com.itbulls.nadine.spring.springbootdemo.repository.*;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.EmailService;
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

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    @Autowired
    public EventController(EventRepository eventRepository, SeatRepository seatRepository, EventService eventService,  BookingRepository bookingRepository,
            EmailService emailService)  {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.eventService = eventService;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
 // ...
    @PostMapping("/upload")
    public ResponseEntity<?> createEvent(
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam(required = false) String status,
        @RequestParam Long categoryId,
        @RequestParam Long locationId,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;

            if (startDate != null && !startDate.isEmpty()) {
                startDateTime = LocalDateTime.parse(startDate, formatter);
            }
            if (endDate != null && !endDate.isEmpty()) {
                endDateTime = LocalDateTime.parse(endDate, formatter);
            }

            if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
                return ResponseEntity.badRequest().body("Start date must be before end date.");
            }

            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            Optional<Location> locationOpt = locationRepository.findById(locationId);

            if (!categoryOpt.isPresent() || !locationOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Category or Location not found");
            }

            Event event = new Event();
            event.setTitle(title);
            event.setDescription(description);

            if (status == null || status.isEmpty()) {
                event.setStatus(EventStatus.DRAFT);
            } else {
                try {
                    event.setStatus(EventStatus.valueOf(status.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Invalid status value.");
                }
            }

            event.setStartDate(startDateTime);
            event.setEndDate(endDateTime);
            event.setCategory(categoryOpt.get());
            event.setLocation(locationOpt.get());

            if (file != null && !file.isEmpty()) {
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.write(filePath, file.getBytes());

                event.setImageUrl("http://localhost:8081/uploads/" + fileName);
            } else {
                event.setImageUrl(null);
            }

            Event savedEvent = eventRepository.save(event);
            return ResponseEntity.ok(savedEvent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating event: " + e.getMessage());
        }
    }




//  //  @PutMapping("/update-status/{id}")
//   // public ResponseEntity<Event> updateStatus(@PathVariable Long id) {
//       // Optional<Event> eventOpt = eventRepository.findById(id);
//       /// if (!eventOpt.isPresent()) {
//         //   return ResponseEntity.notFound().build();
//      //  }
//
//       // Event event = eventOpt.get();
//
//       // LocalDateTime now = LocalDateTime.now();
//       // if (event.getStartDate() != null && event.getEndDate() != null) {
//            if (event.getStartDate().isAfter(now)) {
//                event.setStatus("upcoming");
//            } else if (event.getEndDate().isBefore(now)) {
//                event.setStatus("past");
//            } else {
//                event.setStatus("active");
//            }
//        } else {
//            event.setStatus("draft");
//        }
//
//        eventRepository.save(event);
//        return ResponseEntity.ok(event);
//    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> upcomingEvents = eventRepository.findByStatus(EventStatus.UPCOMING);
        return ResponseEntity.ok(upcomingEvents);
    }

    @GetMapping("/upcoming-with-booking")
    public ResponseEntity<?> getUpcomingEventsWithBooking(@RequestParam Long userId) {
        List<Event> upcomingEvents = eventService.getUpcomingPublishedEvents();
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
    public ResponseEntity<?> updateEvent(
        @PathVariable Long id,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam Long categoryId,
        @RequestParam Long locationId,
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            Optional<Event> eventOpt = eventRepository.findById(id);
            if (!eventOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Event event = eventOpt.get();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

            // تحديث البيانات
            event.setTitle(title);
            event.setDescription(description);
            event.setStartDate(startDateTime);
            event.setEndDate(endDateTime);

            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            Optional<Location> locationOpt = locationRepository.findById(locationId);

            if (!categoryOpt.isPresent() || !locationOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Category or Location not found");
            }
            event.setCategory(categoryOpt.get());
            event.setLocation(locationOpt.get());

            // تحديث الصورة إذا أرسل ملف
            if (file != null && !file.isEmpty()) {
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.write(filePath, file.getBytes());

                event.setImageUrl("http://localhost:8081/uploads/" + fileName);
            }

            Event updated = eventRepository.save(event);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating event: " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishEvent(@PathVariable Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (!eventOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventOpt.get();
        if (!event.getStatus().equals(EventStatus.DRAFT)) {
            return ResponseEntity.badRequest().body("Only draft events can be published");
        }

        if (event.getTitle() == null || event.getTitle().isEmpty()
            || event.getStartDate() == null
            || event.getEndDate() == null
            || event.getStartDate().isAfter(event.getEndDate())
            || event.getCategory() == null
            || event.getLocation() == null
            || event.getImageUrl() == null || event.getImageUrl().isEmpty()) {

            return ResponseEntity.badRequest().body("Event is incomplete and cannot be published.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (event.getStartDate().isAfter(now)) {
            event.setStatus(EventStatus.UPCOMING);
        } else if (!event.getEndDate().isBefore(now)) {
            event.setStatus(EventStatus.ACTIVE);
        } else {
            event.setStatus(EventStatus.PAST);
        }

        event.setPublished(true);
        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }

//    @GetMapping("/drafts/old")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
//    public ResponseEntity<List<Event>> getOldDraftEvents() {
//        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
//        List<Event> oldDrafts = eventRepository.findByStatusAndCreatedAtBefore("draft", sevenDaysAgo);
//        return ResponseEntity.ok(oldDrafts);
//    }


    @GetMapping("/{eventId}/sections")
    public ResponseEntity<List<Section>> getSectionsByEventId(@PathVariable Long eventId) {
        List<Section> sections = sectionRepository.findByEventId(eventId);
        return sections.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(sections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            Optional<Event> eventOpt = eventRepository.findById(id);
            if (!eventOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            Event event = eventOpt.get();

            // 🔥 تحويل إلى DTO لإرجاع category أيضًا
            EventDTO eventDTO = convertToDTO(event);

            return ResponseEntity.ok(eventDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/by-status")
    public ResponseEntity<List<EventDTO>> getEventsByStatus(@RequestParam(required = false) List<String> status) {
        List<Event> events;
        if (status != null && !status.isEmpty()) {
            List<EventStatus> statuses = status.stream()
                .map(s -> EventStatus.valueOf(s.toUpperCase()))
                .collect(Collectors.toList());
            events = eventRepository.findByStatusIn(statuses);
        } else {
            events = eventRepository.findAll();
        }

        List<EventDTO> eventDTOs = events.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventDTOs);
    }


    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Event>> getEventsByCategory(@PathVariable Long categoryId) {
        List<Event> events = eventRepository.findByCategory_Id(categoryId);
        return events.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(events);
    }
    @GetMapping("/count")
    public Map<String, Long> getEventCounts() {
        Map<String, Long> eventCounts = new HashMap<>();
        eventCounts.put("active", eventRepository.countByStatus(EventStatus.ACTIVE));
        eventCounts.put("draft", eventRepository.countByStatus(EventStatus.DRAFT));
        eventCounts.put("past", eventRepository.countByStatus(EventStatus.PAST));
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
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        System.out.println("Status before: " + event.getStatus());
        event.setStatus(EventStatus.CANCELLED);
        System.out.println("Status after set: " + event.getStatus());

        event.setPublished(false);
        eventRepository.save(event);

        Event updated = eventRepository.findById(id).orElseThrow();
        System.out.println("Status after save: " + updated.getStatus());

        return ResponseEntity.ok("Event cancelled successfully and users notified by email.");
    }


    @GetMapping("/{id}/booking-count")
    public ResponseEntity<Integer> getBookingCount(@PathVariable Long id) {
        int count = bookingRepository.countByEventId(id);
        return ResponseEntity.ok(count);
    }


    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setStatus(EventStatus.ARCHIVED);
        eventRepository.save(event);

        return ResponseEntity.ok("Event archived successfully");
    }

    
    private EventDTO convertToDTO(Event event) {
        Category category = event.getCategory();
        CategoryDTO categoryDTO = new CategoryDTO(
            category.getId(),
            category.getName(),
            category.getImageUrl()
        );

        Location location = event.getLocation();
        LocationDTO locationDTO = new LocationDTO(
            location.getId(),
            location.getVenueName()
        );

        return new EventDTO(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getStatus().name(),
            event.getStartDate(),
            event.getEndDate(),
            event.getImageUrl(),
            categoryDTO,
            locationDTO  // ✅ مرر location هنا
        );
    }

    
    @GetMapping("/events-bookings")
    public List<EventBookingStats> getEventsBookings() {
        return eventRepository.findEventBookingStats();
    }

}
