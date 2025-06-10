package com.itbulls.nadine.spring.springbootdemo.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.itbulls.nadine.spring.springbootdemo.model.BookingStatus;

public class BookingDTO {

    private Long id;
    private BookingStatus status;

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Double price;
    private Boolean confirmed;
    private String paymentMethod;

    private EventSummaryDTO event;
    private List<SeatSummaryDTO> seats;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<SeatSummaryDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatSummaryDTO> seats) {
        this.seats = seats;
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
        private boolean reserved;
        private String color;
        private Integer row;
        private Integer number;
        private Double price;  // أضف السعر هنا

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }


        public SeatSummaryDTO() {}

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

        public boolean isReserved() {
            return reserved;
        }

        public void setReserved(boolean reserved) {
            this.reserved = reserved;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Integer getRow() {
            return row;
        }

        public void setRow(Integer row) {
            this.row = row;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "SeatSummaryDTO{" +
                    "id=" + id +
                    ", code='" + code + '\'' +
                    ", reserved=" + reserved +
                    ", color='" + color + '\'' +
                    ", row=" + row +
                    ", number=" + number +
                          ", price=" + price +
                    '}';
        }
    }

}
