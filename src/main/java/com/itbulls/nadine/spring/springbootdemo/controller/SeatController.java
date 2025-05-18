package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.SeatService;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "*")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private BookingService bookingService;

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

}
