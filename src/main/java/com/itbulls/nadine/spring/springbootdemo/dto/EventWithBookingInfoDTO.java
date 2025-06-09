package com.itbulls.nadine.spring.springbootdemo.dto;

import java.time.LocalDateTime;

public class EventWithBookingInfoDTO {

    private Long id;
    private String title;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean alreadyBooked;
    private String bookingStatus;
    private boolean bookingExpired;
    
    private Long bookingId; // أو Integer حسب نوع الـ ID

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }


    // Constructors
    public EventWithBookingInfoDTO() {}

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isAlreadyBooked() {
        return alreadyBooked;
    }

    public void setAlreadyBooked(boolean alreadyBooked) {
        this.alreadyBooked = alreadyBooked;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public boolean isBookingExpired() {
        return bookingExpired;
    }

    public void setBookingExpired(boolean bookingExpired) {
        this.bookingExpired = bookingExpired;
    }
}
