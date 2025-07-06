package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.BookingDTO;
import com.itbulls.nadine.spring.springbootdemo.exception.SeatsAlreadyReservedException;
import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.BookingStatus;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Location;
import com.itbulls.nadine.spring.springbootdemo.model.Notification;
import com.itbulls.nadine.spring.springbootdemo.model.Payment;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.NotificationRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.PaymentRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.EmailService;
import com.itbulls.nadine.spring.springbootdemo.service.EventService;
import com.itbulls.nadine.spring.springbootdemo.service.JwtService;
import com.itbulls.nadine.spring.springbootdemo.service.TicketGeneratorService;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
	  private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Lazy
    @Autowired
    private BookingService bookingService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationRepository notificationRepository;


    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private EventService eventService;


    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;



    @Autowired
    private TicketGeneratorService ticketGeneratorService;
    
    private Long eventId;

    public void someMethod() {
        System.out.println(eventId);  // هنا eventId معروف لأنه خاصية في الكلاس
    }

    public static class BookingRequest {
     
        private Long eventId;
        private List<Long> seatIds;
        private boolean payNow;

       

        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }

        public List<Long> getSeatIds() { return seatIds; }
        public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }

        public boolean isPayNow() { return payNow; }
        public void setPayNow(boolean payNow) { this.payNow = payNow; }
    }
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Event event = eventRepository.findById(bookingRequest.getEventId()).orElse(null);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        List<Seat> seats = seatRepository.findAllById(bookingRequest.getSeatIds());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lockUntil = now.plusMinutes(24);

        for (Seat seat : seats) {
            if (!seat.getSection().getEvent().getId().equals(event.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid seat for this event");
            }
            if (seat.isReserved() || (seat.isLocked() && seat.getLockedUntil() != null && seat.getLockedUntil().isAfter(now))) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Seat " + seat.getCode() + " is already reserved or locked.");
            }
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setSeats(seats);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setNumberOfSeats(seats.size());
        booking.setBookingTime(now);
        booking.setStatus(BookingStatus.PENDING);
        booking.setExpiresAt(lockUntil);

        double totalPrice = seats.stream().mapToDouble(Seat::getPrice).sum();
        booking.setPrice(totalPrice);

        for (Seat seat : seats) {
            seat.setLocked(true);
            seat.setLockedUntil(lockUntil);
            seat.setReserved(false);
            seat.setBooking(booking);
        }

        bookingRepository.save(booking);

        // === هنا نضيف إشعار جديد للادمن ===
        Notification notification = new Notification();
        notification.setTitle("New Booking Created");
        notification.setMessage("User " + user.getEmail() + " booked " + seats.size() + " seat(s) for event " + event.getTitle() + ".");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        notificationRepository.save(notification);

        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getId(),
                "message", "Booking created and seats locked for 24 hours. Please complete payment to confirm."
        ));
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestParam Long bookingId, @RequestParam String paymentMethod) {
        logger.info("Received confirmPayment request with bookingId={} and paymentMethod={}", bookingId, paymentMethod);

        Optional<Booking> optBooking = bookingService.getBookingById(bookingId);
        if (optBooking.isEmpty()) {
            logger.warn("Booking with id {} not found", bookingId);
            return ResponseEntity.badRequest().body("Booking not found");
        }

        Booking booking = optBooking.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            logger.warn("No authentication found");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = auth.getName();
        logger.info("Authenticated user email: {}", email);

        if (!booking.getUser().getEmail().equals(email)) {
            logger.warn("User {} unauthorized to confirm booking with id {}", email, bookingId);
            return ResponseEntity.status(403).body("Unauthorized to confirm this booking.");
        }

        // السماح بتأكيد الدفع فقط إذا الحالة UNPAID أو PENDING
        if (booking.getStatus() != BookingStatus.UNPAID && booking.getStatus() != BookingStatus.PENDING) {
            logger.warn("Booking with id {} is not in UNPAID or PENDING status. Current status: {}", bookingId, booking.getStatus());
            return ResponseEntity.badRequest().body("Booking is not in a state to confirm payment.");
        }

        booking.setStatus(BookingStatus.PAID);
        bookingService.saveBooking(booking);

        logger.info("Payment confirmed for booking id {}", bookingId);
        return ResponseEntity.ok("Payment confirmed. Booking is now PAID and waiting for admin confirmation.");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBookingByAdmin(@PathVariable Long id) {
        Optional<Booking> optBooking = bookingService.getBookingById(id);
        if (optBooking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = optBooking.get();

        if (booking.getStatus() != BookingStatus.PAID) {
            return ResponseEntity.badRequest().body("Booking must be PAID before confirmation.");
        }

     // Confirm booking by admin
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmed(true);
        bookingService.saveBooking(booking);

        // Generate ticket PDF
        byte[] pdf = ticketGeneratorService.generateTicket(booking);

        // Send confirmation email with attached ticket
        emailService.sendBookingConfirmationWithPDF(
            booking.getUser().getEmail(),
            "Booking Confirmed",
            "Your booking has been successfully confirmed. Please find the attached ticket.",
            pdf
        );


        // أرجع الحجز بعد التحديث لكي يتم عرضه في الواجهة مباشرة
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkIfBookingExists(@RequestParam Long eventId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            String email = auth.getName();
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            List<Booking> bookings = bookingService.getBookingsByUserIdAndEventId(user.getId(), eventId);

            if (bookings.isEmpty()) {
                System.out.println("No bookings found for user and event");
                return ResponseEntity.ok().body(new ArrayList<>()); // empty list
            } else {
                List<BookingDTO> bookingDTOs = bookings.stream()
                    .map(this::mapToBookingDTO)
                    .collect(Collectors.toList());

                System.out.println("Found bookings: " + bookingDTOs.size());
                return ResponseEntity.ok(bookingDTOs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    // بقية الميثودز تبقى كما هي لكن تأكد استخدام BookingStatus enum وعدم استعمال String للحالة.

    private BookingDTO mapToBookingDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());  // fixed variable name here
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setExpiresAt(booking.getExpiresAt());
        dto.setPrice(booking.getPrice());
        dto.setConfirmed(booking.getConfirmed());

        if (booking.getEvent() != null) {
            BookingDTO.EventSummaryDTO eventDTO = new BookingDTO.EventSummaryDTO();
            eventDTO.setId(booking.getEvent().getId());
            eventDTO.setTitle(booking.getEvent().getTitle());
            eventDTO.setImageUrl(booking.getEvent().getImageUrl());
            eventDTO.setStartDate(booking.getEvent().getStartDate());
            eventDTO.setEndDate(booking.getEvent().getEndDate());
            if (booking.getEvent().getLocation() != null) {
                eventDTO.setLocation(booking.getEvent().getLocation().getVenueName());
            }
            dto.setEvent(eventDTO);
        }

        if (booking.getSeats() != null && !booking.getSeats().isEmpty()) {
            List<BookingDTO.SeatSummaryDTO> seatDTOs = new ArrayList<>();

            for (Seat seat : booking.getSeats()) {
                BookingDTO.SeatSummaryDTO seatDTO = new BookingDTO.SeatSummaryDTO();
                seatDTO.setId(seat.getId());
                seatDTO.setCode(seat.getCode());
                seatDTO.setReserved(seat.isReserved());
                seatDTO.setColor(seat.getColor());
                seatDTO.setRow(seat.getRow());
                seatDTO.setNumber(seat.getNumber());
                seatDTOs.add(seatDTO);
            }

            dto.setSeats(seatDTOs);  // <-- الصحيح
        }


        return dto;
    }
    
//    @GetMapping("/{bookingId}")
//    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
//        Optional<Booking> optional = bookingService.getBookingById(bookingId);
//        return optional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookingDTOs();
        return ResponseEntity.ok(bookings);
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
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestParam BookingStatus newStatus) {
        Optional<Booking> optionalBooking = bookingService.getBookingById(id);
        if (optionalBooking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = optionalBooking.get();

        // تحقق من صلاحية الانتقال للحالة الجديدة إن لزم
        if (booking.getStatus() == newStatus) {
            return ResponseEntity.badRequest().body("Booking is already in the requested status.");
        }

        booking.setStatus(newStatus);
        if (newStatus == BookingStatus.CONFIRMED) {
            booking.setConfirmed(true);
        }

        bookingService.saveBooking(booking);
        return ResponseEntity.ok("Booking status updated successfully to " + newStatus.name());
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(
            @RequestBody BookingRequest bookingRequest,
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtService.extractUsername(token.substring(7));
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("User not found");
            }

            List<Booking> bookings = bookingService.createBooking(
                    user.getId(), bookingRequest.getEventId(), bookingRequest.getSeatIds());

            if (bookingRequest.isPayNow()) {
                bookingService.confirmPayment(bookings);
            }

            return ResponseEntity.ok(bookings);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
    }

    @PostMapping("/pay/{bookingId}")
    public ResponseEntity<?> payBooking(
            @PathVariable Long bookingId,
            @RequestHeader("Authorization") String token) {
        try {
            // تحقق من المستخدم

            Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                return ResponseEntity.badRequest().body("Booking already paid");
            }

            // تنفيذ الدفع (مثلاً استدعاء خدمة دفع)

            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            return ResponseEntity.ok("Payment confirmed");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long id) {
        System.out.println("Trying to fetch booking with ID: " + id);

        Booking booking = bookingRepository.findByIdWithUser(id)
        	    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));


        System.out.println("Booking found: " + booking);

        BookingDTO dto = convertToDTO(booking);
        System.out.println("Converted BookingDTO: " + dto);

        return ResponseEntity.ok(dto);
    }

    private BookingDTO convertToDTO(Booking booking) {
        System.out.println("Converting booking to DTO...");

        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setExpiresAt(booking.getExpiresAt());
        dto.setPrice(booking.getPrice());
        dto.setConfirmed(booking.getConfirmed());

        // Seats info
        if (booking.getSeats() != null && !booking.getSeats().isEmpty()) {
            List<BookingDTO.SeatSummaryDTO> seatDTOs = booking.getSeats().stream().map(seat -> {
                BookingDTO.SeatSummaryDTO dtoSeat = new BookingDTO.SeatSummaryDTO();
                dtoSeat.setId(seat.getId());
                dtoSeat.setCode(seat.getCode());
                dtoSeat.setReserved(seat.isReserved());
                dtoSeat.setColor(seat.getColor());
                dtoSeat.setRow(seat.getRow());
                dtoSeat.setPrice(seat.getPrice());
                dtoSeat.setNumber(seat.getNumber());
                return dtoSeat;
            }).collect(Collectors.toList());

            dto.setSeats(seatDTOs);
            System.out.println("Seats info set: " + seatDTOs);
        } else {
            System.out.println("Booking has no seats assigned.");
        }

        // Event info
        if (booking.getEvent() != null) {
            BookingDTO.EventSummaryDTO eventDTO = new BookingDTO.EventSummaryDTO();
            eventDTO.setId(booking.getEvent().getId());
            eventDTO.setTitle(booking.getEvent().getTitle());

            Location loc = booking.getEvent().getLocation();
            if (loc != null) {
                eventDTO.setLocation(loc.getVenueName());
            }

            eventDTO.setImageUrl(booking.getEvent().getImageUrl());
            eventDTO.setStartDate(booking.getEvent().getStartDate());
            eventDTO.setEndDate(booking.getEvent().getEndDate());

            dto.setEvent(eventDTO);
            System.out.println("Event info set: " + eventDTO);
        } else {
            System.out.println("Booking has no event assigned.");
        }

        // User info
        if (booking.getUser() != null) {
            BookingDTO.UserSummaryDTO userDTO = new BookingDTO.UserSummaryDTO();
            userDTO.setId(booking.getUser().getId());
            userDTO.setUsername(booking.getUser().getUsername());
            userDTO.setEmail(booking.getUser().getEmail());
            dto.setUser(userDTO);
            System.out.println("User info set: " + userDTO);
        } else {
            System.out.println("Booking has no user assigned.");
        }

     // Payment receipt image
        Payment payment = paymentRepository.findByBookingId(booking.getId());
        if (payment != null) {
            dto.setPaymentMethod(payment.getPaymentMethod());
            if (payment.getReceiptImagePath() != null) {
                String rawPath = payment.getReceiptImagePath();
                String webPath = rawPath.replace("\\", "/");
                dto.setReceiptImageUrl("/" + webPath);
            }
        }

        return dto;
    }

    @GetMapping("/mybookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyBookings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = auth.getName();
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        List<Booking> bookings = bookingService.getBookingsForUser(user.getId());
        List<BookingDTO> bookingDTOs = bookings.stream()
                                              .map(this::mapToBookingDTO)
                                              .collect(Collectors.toList());

        return ResponseEntity.ok(bookingDTOs);
    }
    
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            Booking cancelledBooking = bookingService.cancelBooking(bookingId); // يفترض يعيد الـ booking بعد الإلغاء

            // إرسال إيميل إشعار إلغاء
            emailService.sendEmail(
            	    cancelledBooking.getUser().getEmail(),
            	    "Booking Cancelled",
            	    "Dear user, your booking with ID " + bookingId + " has been cancelled. If you have any questions, please contact us."
            	);


            return ResponseEntity.ok(cancelledBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel booking: " + e.getMessage());
        }
    }

    
    @GetMapping("/{id}/ticket")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long id) {
        Optional<Booking> optBooking = bookingService.getBookingById(id);
        if (optBooking.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = optBooking.get();

        byte[] pdf = ticketGeneratorService.generateTicket(booking);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
            .filename("ticket_" + id + ".pdf")
            .build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

}

    
    

   