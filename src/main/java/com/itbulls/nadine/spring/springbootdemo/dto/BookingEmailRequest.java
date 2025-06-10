package com.itbulls.nadine.spring.springbootdemo.dto;

import java.util.List;

public class BookingEmailRequest {
    private Long bookingId;
    private List<TicketInfo> tickets;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public List<TicketInfo> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketInfo> tickets) {
        this.tickets = tickets;
    }
}
