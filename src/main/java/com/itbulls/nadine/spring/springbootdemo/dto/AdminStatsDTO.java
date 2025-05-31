package com.itbulls.nadine.spring.springbootdemo.dto;

public class AdminStatsDTO {

    private long categoryCount;
    private long sponsorCount;
    private long eventCount;
    private long userCount;
    private long totalBookingCount;
    private long newBookingCount;
    private long confirmedBookingCount;
    private long cancelledBookingCount;
    private long subscriberCount;

    // Getters and Setters

    public long getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(long categoryCount) {
        this.categoryCount = categoryCount;
    }

    public long getSponsorCount() {
        return sponsorCount;
    }

    public void setSponsorCount(long sponsorCount) {
        this.sponsorCount = sponsorCount;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void setEventCount(long eventCount) {
        this.eventCount = eventCount;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getTotalBookingCount() {
        return totalBookingCount;
    }

    public void setTotalBookingCount(long totalBookingCount) {
        this.totalBookingCount = totalBookingCount;
    }

    public long getNewBookingCount() {
        return newBookingCount;
    }

    public void setNewBookingCount(long newBookingCount) {
        this.newBookingCount = newBookingCount;
    }

    public long getConfirmedBookingCount() {
        return confirmedBookingCount;
    }

    public void setConfirmedBookingCount(long confirmedBookingCount) {
        this.confirmedBookingCount = confirmedBookingCount;
    }

    public long getCancelledBookingCount() {
        return cancelledBookingCount;
    }

    public void setCancelledBookingCount(long cancelledBookingCount) {
        this.cancelledBookingCount = cancelledBookingCount;
    }

    public long getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(long subscriberCount) {
        this.subscriberCount = subscriberCount;
    }
}
