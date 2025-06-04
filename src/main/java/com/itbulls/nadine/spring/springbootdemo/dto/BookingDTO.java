package com.itbulls.nadine.spring.springbootdemo.dto;

import java.time.LocalDateTime;

public class BookingDTO {

    private Long id;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Double price;
    private Boolean confirmed;
    private String paymentMethod;

    private EventSummaryDTO event;
    private SeatSummaryDTO seat;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public EventSummaryDTO getEvent() {
        return event;
    }

    public void setEvent(EventSummaryDTO event) {
        this.event = event;
    }

    public SeatSummaryDTO getSeat() {
        return seat;
    }

    public void setSeat(SeatSummaryDTO seat) {
        this.seat = seat;
    }

    // ---------------------------------------
    // Inner Class: EventSummaryDTO
    // ---------------------------------------
    public static class EventSummaryDTO {
        private Long id;
        private String title;
        private String location;
        private String imageUrl;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

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

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
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
    }

    // ---------------------------------------
    // Inner Class: SeatSummaryDTO
    // ---------------------------------------
    public static class SeatSummaryDTO {
        private Long id;
        private String code;
        private Boolean reserved;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Boolean getReserved() {
            return reserved;
        }

        public void setReserved(Boolean reserved) {
            this.reserved = reserved;
        }
    }
}
