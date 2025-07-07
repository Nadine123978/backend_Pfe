package com.itbulls.nadine.spring.springbootdemo.dto;

public class EventBookingCountDTO {
    private String eventName;
    private long bookingsCount;

    public EventBookingCountDTO(String eventName, long bookingsCount) {
        this.eventName = eventName;
        this.bookingsCount = bookingsCount;
    }

    // getters and setters
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getBookingsCount() {
        return bookingsCount;
    }

    public void setBookingsCount(long bookingsCount) {
        this.bookingsCount = bookingsCount;
    }
}
