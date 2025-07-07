package com.itbulls.nadine.spring.springbootdemo.dto;

public class CategoryBookingCountDTO {
    private String categoryName;
    private Long bookingsCount;

    public CategoryBookingCountDTO(String categoryName, Long bookingsCount) {
        this.categoryName = categoryName;
        this.bookingsCount = bookingsCount;
    }

    // Getters & Setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getBookingsCount() {
        return bookingsCount;
    }

    public void setBookingsCount(Long bookingsCount) {
        this.bookingsCount = bookingsCount;
    }
}
