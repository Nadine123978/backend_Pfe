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
import java.util.Optional;

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

    // جلب جميع المقاعد
    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        List<Seat> seats = seatService.getAllSeats();
        return ResponseEntity.ok(seats);
    }

    // جلب مقعد حسب الـ id
    @GetMapping("/{id}")
    public ResponseEntity<?> getSeatById(@PathVariable Long id) {
    	return seatService.getSeatById(id)
    	        .map(ResponseEntity::ok)
    	        .orElseGet(() -> ResponseEntity.badRequest().body(null)); // لكن هيرجع ResponseEntity<Seat> فارغ

    }

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

    // إنشاء مقعد جديد مع ربطه بقسم
    @PostMapping
    public ResponseEntity<?> createSeat(@RequestParam Long sectionId, @RequestBody Seat seat) {
        Section section = sectionService.getSectionById(sectionId).orElse(null);
        if (section == null) {
            return ResponseEntity.badRequest().body("Section not found");
        }
        seat.setSection(section);

        Seat savedSeat = seatService.save(seat);
        return ResponseEntity.ok(savedSeat);
    }

    // تعديل مقعد موجود
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeat(@PathVariable Long id, @RequestBody Seat updatedSeat) {
        Optional<Seat> optionalSeat = seatService.getSeatById(id);

        if (optionalSeat.isEmpty()) {
            return ResponseEntity.badRequest().body("Seat not found");
        }

        Seat seat = optionalSeat.get();
        seat.setRow(updatedSeat.getRow());
        seat.setNumber(updatedSeat.getNumber());
        seat.setReserved(updatedSeat.isReserved());

        if (updatedSeat.getSection() != null) {
            seat.setSection(updatedSeat.getSection());
        }

        Seat saved = seatService.save(seat);
        return ResponseEntity.ok(saved);
    }
    
    @PostMapping("/section/{sectionId}")
    public ResponseEntity<?> addSeatsToSection(
            @PathVariable Long sectionId,
            @RequestBody List<Seat> seats) {

        Section section = sectionService.getSectionById(sectionId).orElse(null);
        if (section == null) {
            return ResponseEntity.badRequest().body("Section not found");
        }

        for (Seat seat : seats) {
            seat.setSection(section);
            seatService.save(seat);
        }

        return ResponseEntity.ok("Seats added to section " + section.getName());
    }




    // حذف مقعد
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeat(@PathVariable Long id) {
        return seatService.getSeatById(id).map(seat -> {
            seatService.delete(seat);
            return ResponseEntity.ok("Seat deleted successfully");
        }).orElse(ResponseEntity.badRequest().body("Seat not found"));
    }

    // توليد المقاعد لقسم معين
    @PostMapping("/generate")
    public ResponseEntity<?> generateSeatsForSection(@RequestParam Long sectionId) {
        Section section = sectionService.getSectionById(sectionId).orElse(null);
        if (section == null) {
            return ResponseEntity.badRequest().body("Section not found");
        }
        seatService.generateSeatsForSection(section);
        return ResponseEntity.ok("Seats generated successfully for section " + section.getName());
    }

    // تأكيد حجز المقاعد
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmSeats(@RequestBody List<Long> seatIds) {
        for (Long id : seatIds) {
            Seat seat = seatService.getSeatById(id).orElse(null);
            if (seat != null && !seat.isReserved()) {
                seat.setReserved(true);
                seatService.save(seat);
            }
        }
        return ResponseEntity.ok("Seats confirmed successfully");
    }
}
