package com.itbulls.nadine.spring.springbootdemo.dto;

public class CheckAvailabilityRequest {
    private int adults;
    private int children;
    private int infants;

    // Getters and setters
    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public int getInfants() {
        return infants;
    }

    public void setInfants(int infants) {
        this.infants = infants;
    }

    public int getTotalRequestedSeats() {
        return adults + children + infants;
    }
}

