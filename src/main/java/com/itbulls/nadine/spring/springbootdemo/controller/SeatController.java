package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.SeatService;
import com.itbulls.nadine.spring.springbootdemo.service.SectionService;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "http://localhost:5173")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    // جلب المقاعد حسب القسم
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<Seat>> getSeatsBySection(@PathVariable Long sectionId) {
        List<Seat> seats = seatService.getSeatsBySection(sectionId);
        return ResponseEntity.ok(seats);
    }

    // تنفيذ حجز مؤقت (Hold) لمقعد
    @PostMapping("/hold")
    public ResponseEntity<?> holdSeat(
            @RequestParam Long seatId,
            @RequestParam Long userId,
            @RequestParam Double price
    ) {
        // تحقق من أن المقعد موجود وغير محجوز
        if (seatService.isSeatReserved(seatId)) {
            return ResponseEntity.badRequest().body("Seat already reserved");
        }

        Seat seat = seatService.getSeatById(seatId).orElse(null);
        User user = userService.getUserById(userId);
        if (seat == null || user == null) {
            return ResponseEntity.badRequest().body("Seat or User not found");
        }


        Booking booking = bookingService.holdSeat(seat, user, price);
        return ResponseEntity.ok(booking);
    }
    @PostMapping
    public ResponseEntity<Seat> createSeat(@RequestBody Seat seat) {
        Seat savedSeat = seatService.save(seat);
        return ResponseEntity.ok(savedSeat);
    }
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateSeatsForSection(@RequestParam Long sectionId) {
        Section section = sectionService.getSectionById(sectionId).orElse(null);
        if (section == null) {
            return ResponseEntity.badRequest().body("Section not found");
        }
        seatService.generateSeatsForSection(section);
        return ResponseEntity.ok("Seats generated successfully for section " + section.getName());
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmSeats(@RequestBody List<Long> seatIds) {
        for (Long id : seatIds) {
            Seat seat = seatService.getSeatById(id).orElse(null);
            if (seat != null && !seat.isReserved()) {
                seat.setReserved(true);
                seatService.save(seat); // أو seatRepository.save(seat);
            }
        }
        return ResponseEntity.ok("Seats confirmed successfully");
    }



}
