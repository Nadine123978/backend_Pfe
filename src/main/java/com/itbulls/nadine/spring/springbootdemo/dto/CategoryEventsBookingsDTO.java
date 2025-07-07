package com.itbulls.nadine.spring.springbootdemo.dto;

public class CategoryEventsBookingsDTO {
    private String categoryName;
    private int eventsCount;
    private int bookingsCount;

    public CategoryEventsBookingsDTO(String categoryName, int eventsCount, int bookingsCount) {
        this.categoryName = categoryName;
        this.eventsCount = eventsCount;
        this.bookingsCount = bookingsCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getEventsCount() {
        return eventsCount;
    }

    public void setEventsCount(int eventsCount) {
        this.eventsCount = eventsCount;
    }

    public int getBookingsCount() {
        return bookingsCount;
    }

    public void setBookingsCount(int bookingsCount) {
        this.bookingsCount = bookingsCount;
    }
}
