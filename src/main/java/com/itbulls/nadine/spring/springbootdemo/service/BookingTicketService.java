package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Booking;
import com.itbulls.nadine.spring.springbootdemo.model.BookingTicket;
import com.itbulls.nadine.spring.springbootdemo.model.TicketType;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingTicketRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.TicketTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingTicketService {

    @Autowired
    private BookingTicketRepository bookingTicketRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    public void createBookingTickets(Long bookingId, List<BookingRequestItem> ticketRequests) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        for (BookingRequestItem item : ticketRequests) {
            TicketType ticketType = ticketTypeRepository.findById(item.getTicketTypeId())
                    .orElseThrow(() -> new RuntimeException("Ticket type not found"));

            BookingTicket bookingTicket = new BookingTicket();
            bookingTicket.setBooking(booking);
            bookingTicket.setTicketType(ticketType);
            bookingTicket.setQuantity(item.getQuantity());

            bookingTicketRepository.save(bookingTicket);
        }
    }

    public static class BookingRequestItem {
        private Long ticketTypeId;
        private int quantity;

        public Long getTicketTypeId() {
            return ticketTypeId;
        }

        public void setTicketTypeId(Long ticketTypeId) {
            this.ticketTypeId = ticketTypeId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
