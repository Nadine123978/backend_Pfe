package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import com.itbulls.nadine.spring.springbootdemo.service.BookingService;
import com.itbulls.nadine.spring.springbootdemo.service.EventService;
import com.itbulls.nadine.spring.springbootdemo.service.SeatService;
import com.itbulls.nadine.spring.springbootdemo.service.SectionService;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private EventService eventService;

    @Autowired
    private SeatRepository seatRepository;

    // جلب جميع المقاعد وتحديث حالة القفل لكل مقعد
    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        List<Seat> seats = seatService.getAllSeats();
        seats.forEach(Seat::updateLockStatus);
        return ResponseEntity.ok(seats);
    }

    // جلب مقعد محدد مع تحديث حالة القفل
    @GetMapping("/{id}")
    public ResponseEntity<?> getSeatById(@PathVariable Long id) {
        Optional<Seat> seatOpt = seatService.getSeatById(id);

        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();
            seat.updateLockStatus();
            return ResponseEntity.ok(seat);
        } else {
            return ResponseEntity.badRequest().body("Seat not found");
        }
    }


    // جلب المقاعد حسب القسم
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<Seat>> getSeatsBySection(@PathVariable Long sectionId) {
        List<Seat> seats = seatService.getSeatsBySection(sectionId);
        return ResponseEntity.ok(seats);
    }

    // تنفيذ حجز مؤقت (Hold) لمقعد معين
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

    // إنشاء مقعد جديد وربطه بقسم وحدث معين
    @PostMapping
    public ResponseEntity<?> createSeat(
            @RequestParam Long sectionId,
            @RequestParam Long eventId,
            @RequestBody Seat seat
    ) {
        Section section = sectionService.getSectionById(sectionId).orElse(null);
        if (section == null) {
            return ResponseEntity.badRequest().body("Section not found");
        }
        seat.setSection(section);

        Seat savedSeat = seatService.save(seat);
        return ResponseEntity.ok(savedSeat);
    }

    // تعديل مقعد موجود حسب المعرف
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
        seat.setPrice(updatedSeat.getPrice());
        seat.setColor(updatedSeat.getColor());

        if (updatedSeat.getSection() != null && updatedSeat.getSection().getId() != null) {
            Optional<Section> sectionOpt = sectionService.getSectionById(updatedSeat.getSection().getId());
            if (sectionOpt.isPresent()) {
                seat.setSection(sectionOpt.get());
            } else {
                return ResponseEntity.badRequest().body("Section not found");
            }
        }

        Seat saved = seatService.save(seat);

        return ResponseEntity.ok(saved);
    }

    // إضافة قائمة كراسي لقسم معين
    @PostMapping("/section/{sectionId}/generate")
    public ResponseEntity<?> generateSeatsInSection(
            @PathVariable Long sectionId,
            @RequestBody List<Seat> seats
    ) {
        Section section = sectionService.getSectionById(sectionId).orElse(null);
        if (section == null) {
            return ResponseEntity.badRequest().body("Section not found");
        }

        seats.forEach(seat -> seat.setSection(section)); // ربط كل مقعد بالقسم
        seatService.saveAll(seats);

        return ResponseEntity.ok("Generated " + seats.size() + " seats in section " + section.getName());
    }


    // حذف مقعد معين
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeat(@PathVariable Long id) {
        return seatService.getSeatById(id).map(seat -> {
            seatService.delete(seat);
            return ResponseEntity.ok("Seat deleted successfully");
        }).orElse(ResponseEntity.badRequest().body("Seat not found"));
    }

    // توليد كراسي لقسم معين (يمكن تعديل المنطق داخل الخدمة)
    @PostMapping("/generate")
    public ResponseEntity<?> generateSeatsForSection(@RequestParam Long sectionId) {
        Section section = sectionService.getSectionById(sectionId).orElse(null);
        if (section == null) {
            return ResponseEntity.badRequest().body("Section not found");
        }
        seatService.generateSeatsForSection(section);
        return ResponseEntity.ok("Seats generated successfully for section " + section.getName());
    }

    // تأكيد حجز مجموعة من المقاعد
    @Transactional
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmSeats(@RequestBody List<Long> seatIds) {
        LocalDateTime now = LocalDateTime.now();

        for (Long id : seatIds) {
            Seat seat = seatService.getSeatById(id).orElse(null);
            if (seat == null) continue;

            if (seat.isReserved()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Seat already reserved.");
            }

            if (!seat.isLocked() || seat.getLockedUntil() == null || seat.getLockedUntil().isBefore(now)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Seat lock expired or not locked.");
            }

            seat.setReserved(true);
            seat.setLocked(false);  // remove lock
            seat.setLockedUntil(null);
            seatService.save(seat);
        }

        return ResponseEntity.ok("Seats confirmed successfully");
    }


    // قفل مجموعة من المقاعد لمدة 5 دقائق
    @Transactional
    @PostMapping("/lock")
    public ResponseEntity<?> lockSeats(@RequestBody List<Long> seatIds) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lockExpireTime = now.plusMinutes(5);

        List<Seat> seats = seatRepository.findAllById(seatIds);

        for (Seat seat : seats) {
            if (seat.isReserved() || (seat.isLocked() && seat.getLockedUntil() != null && seat.getLockedUntil().isAfter(now))) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Seat " + seat.getCode() + " is already reserved or locked.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
        }

        seats.forEach(seat -> {
            seat.setLocked(true);
            seat.setLockedUntil(lockExpireTime);
        });

        seatRepository.saveAll(seats);

        Map<String, String> success = new HashMap<>();
        success.put("message", "Seats locked successfully.");
        return ResponseEntity.ok(success);
    }

    // فك قفل مجموعة من المقاعد
    @PostMapping("/unlock")
    public ResponseEntity<?> unlockSeats(@RequestBody List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        seats.forEach(seat -> {
            seat.setLocked(false);
            seat.setLockedUntil(null);
        });

        seatRepository.saveAll(seats);

        return ResponseEntity.ok("Seats unlocked successfully.");
    }
}
