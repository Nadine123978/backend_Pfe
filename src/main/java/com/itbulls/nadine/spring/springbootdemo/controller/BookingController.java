package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.BookingDTO;
import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.EmailService;
import com.itbulls.nadine.spring.springbootdemo.service.TicketGeneratorService;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Lazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

	@Lazy
	@Autowired
	private BookingService bookingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private TicketGeneratorService ticketGeneratorService;

    public static class BookingRequest {
        private Long eventId;

        public Long getEventId() {
            return eventId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            boolean alreadyHasBooking = bookingService.hasActiveBooking(user.getId(), bookingRequest.getEventId());
            if (alreadyHasBooking) {
                return ResponseEntity.badRequest().body("You already have a pending booking for this event.");
            }

            Booking booking = bookingService.createBooking(user.getId(), bookingRequest.getEventId());
            return ResponseEntity.ok(mapToBookingDTO(booking));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        List<BookingDTO> bookingDTOs = bookingService.getBookingsForUser(user.getId())
                .stream()
                .map(this::mapToBookingDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingDTOs);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        Optional<Booking> optional = bookingService.getBookingById(bookingId);
        return optional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        Optional<Booking> optional = bookingService.getBookingById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestParam String status) {
        Optional<Booking> optional = bookingService.getBookingById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = optional.get();
        booking.setStatus(status);
        bookingService.saveBooking(booking);

        return ResponseEntity.ok("Status updated to: " + status);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(
            @RequestParam Long bookingId,
            @RequestParam String paymentMethod) {
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.badRequest().body("Booking not found");
        }

        Booking booking = optionalBooking.get();
        booking.setConfirmed(true);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus("CONFIRMED");
        bookingService.saveBooking(booking);

        byte[] pdf = ticketGeneratorService.generateTicket(booking);

        emailService.sendBookingConfirmationWithPDF(
            booking.getUser().getEmail(),
            "تم تأكيد الحجز",
            "تم حجز مقعدك بنجاح، تجد التذكرة مرفقة.",
            pdf
        );

        return ResponseEntity.ok("Booking confirmed and PDF sent to email.");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(
            @RequestParam Long bookingId,
            @RequestParam String paymentMethod) {
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.badRequest().body("Booking not found");
        }

        Booking booking = optionalBooking.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        if (!booking.getUser().getEmail().equals(email)) {
            return ResponseEntity.status(403).body("Unauthorized to confirm this booking.");
        }

        booking.setConfirmed(true);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus("CONFIRMED");
        bookingService.saveBooking(booking);

        return ResponseEntity.ok("Payment confirmed and booking marked as confirmed.");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('USER')")
    @GetMapping("/{bookingId}/ticket")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long bookingId) {
        Optional<Booking> optional = bookingService.getBookingById(bookingId);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = optional.get();
        byte[] pdf = ticketGeneratorService.generateTicket(booking);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket_" + bookingId + ".pdf")
            .body(pdf);
    }

    private BookingDTO mapToBookingDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();

        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setExpiresAt(booking.getExpiresAt());
        dto.setPrice(booking.getPrice());
        dto.setConfirmed(booking.getConfirmed());
        dto.setPaymentMethod(booking.getPaymentMethod());

        if (booking.getEvent() != null) {
            BookingDTO.EventSummaryDTO eventDTO = new BookingDTO.EventSummaryDTO();
            eventDTO.setId(booking.getEvent().getId());
            eventDTO.setTitle(booking.getEvent().getTitle());
            eventDTO.setImageUrl(booking.getEvent().getImageUrl());
            eventDTO.setStartDate(booking.getEvent().getStartDate());
            eventDTO.setEndDate(booking.getEvent().getEndDate());
            if (booking.getEvent().getLocation() != null) {
                eventDTO.setLocation(booking.getEvent().getLocation().getFullAddress());
            }
            dto.setEvent(eventDTO);
        }

        if (booking.getSeat() != null) {
            BookingDTO.SeatSummaryDTO seatDTO = new BookingDTO.SeatSummaryDTO();
            seatDTO.setId(booking.getSeat().getId());
            seatDTO.setCode(booking.getSeat().getCode());
            seatDTO.setReserved(booking.getSeat().isReserved());
            dto.setSeat(seatDTO);
        }

        return dto;
    }
}
