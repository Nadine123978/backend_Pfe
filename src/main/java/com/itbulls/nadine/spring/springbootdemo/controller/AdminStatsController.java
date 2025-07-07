package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.AdminStatsDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.CategoryBookingCountDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.CategoryEventsBookingsDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.EventBookingCountDTO;
import com.itbulls.nadine.spring.springbootdemo.model.BookingStatus;
import com.itbulls.nadine.spring.springbootdemo.repository.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminStatsController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @GetMapping("/stats")
    public AdminStatsDTO getAdminStats() {
        AdminStatsDTO stats = new AdminStatsDTO();
        stats.setCategoryCount(categoryRepository.count());
        stats.setSponsorCount(sponsorRepository.count());
        stats.setEventCount(eventRepository.count());
        stats.setUserCount(userRepository.count());

        stats.setTotalBookingCount(bookingRepository.count());
        stats.setNewBookingCount(bookingRepository.countByStatus(BookingStatus.NEW));
        stats.setConfirmedBookingCount(bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        stats.setCancelledBookingCount(bookingRepository.countByStatus(BookingStatus.CANCELLED));


        stats.setSubscriberCount(subscriberRepository.count());

        return stats;
    }
    @GetMapping("/bookings-per-event")
    public List<EventBookingCountDTO> getBookingsPerEvent() {
        List<Object[]> results = bookingRepository.countBookingsGroupedByEvent();

        return results.stream()
            .map(r -> new EventBookingCountDTO((String) r[0], (Long) r[1]))
            .toList();
    }
    
    @GetMapping("/events-and-bookings-per-category")
    public List<CategoryEventsBookingsDTO> getEventsAndBookingsPerCategory() {
        List<Object[]> results = bookingRepository.countEventsAndBookingsGroupedByCategory();

        return results.stream()
            .map(r -> new CategoryEventsBookingsDTO(
                (String) r[0],
                ((Long) r[1]).intValue(),
                ((Long) r[2]).intValue()
            ))
            .toList();
    }



    
}
