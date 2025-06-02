package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.service.BookingTicketService;
import com.itbulls.nadine.spring.springbootdemo.service.BookingTicketService.BookingRequestItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-tickets")
public class BookingTicketController {

    @Autowired
    private BookingTicketService bookingTicketService;

    @PostMapping("/{bookingId}")
    public String createBookingTickets(
            @PathVariable Long bookingId,
            @RequestBody List<BookingRequestItem> ticketRequests) {
        bookingTicketService.createBookingTickets(bookingId, ticketRequests);
        return "Tickets added to booking successfully.";
    }
}
