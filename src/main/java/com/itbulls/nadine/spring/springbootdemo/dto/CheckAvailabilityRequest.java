package com.itbulls.nadine.spring.springbootdemo.dto;

public class CheckAvailabilityRequest {
    private int totalRequestedSeats;

    public CheckAvailabilityRequest() {}

    public CheckAvailabilityRequest(int totalRequestedSeats) {
        this.totalRequestedSeats = totalRequestedSeats;
    }

    public int getTotalRequestedSeats() {
        return totalRequestedSeats;
    }

    public void setTotalRequestedSeats(int totalRequestedSeats) {
        this.totalRequestedSeats = totalRequestedSeats;
    }
}
