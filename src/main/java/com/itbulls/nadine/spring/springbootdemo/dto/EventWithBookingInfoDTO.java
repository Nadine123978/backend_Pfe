package com.itbulls.nadine.spring.springbootdemo.dto;

import java.time.LocalDateTime;

import com.itbulls.nadine.spring.springbootdemo.model.Event;

public class EventWithBookingInfoDTO {

    private Long id;
    private String title;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String location;        // ✅ الاسم الكامل للمكان
    private String priceRange;      // ✅ صيغة مثل "30.00 - 120.00"

    private boolean alreadyBooked;
    private String bookingStatus;
    private boolean bookingExpired;
    private Long bookingId;

    // ✅ Constructor with Event parameter
    public EventWithBookingInfoDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.imageUrl = event.getImageUrl();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();

        // ✅ الموقع
        this.location = event.getLocation() != null ? event.getLocation().getVenueName() : "N/A";

        // ✅ حساب السعر الأدنى والأعلى من الـ Sections
        Double min = event.getMinPrice();
        Double max = event.getMaxPrice();
        if (min != null && max != null) {
            if (min.equals(max)) {
                this.priceRange = String.format("%.2f", min);
            } else {
                this.priceRange = String.format("%.2f - %.2f", min, max);
            }
        } else {
            this.priceRange = "N/A";
        }
    }

    // ✅ Default constructor
    public EventWithBookingInfoDTO() {}

    // ✅ Getters & Setters
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

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }
}
