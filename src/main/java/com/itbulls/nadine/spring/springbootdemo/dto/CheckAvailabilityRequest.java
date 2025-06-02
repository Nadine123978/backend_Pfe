package com.itbulls.nadine.spring.springbootdemo.dto;

public class CheckAvailabilityRequest {

    private int adults;
    private int children;
    private int infants;

    public CheckAvailabilityRequest() {
    }

    public CheckAvailabilityRequest(int adults, int children, int infants) {
        this.adults = adults;
        this.children = children;
        this.infants = infants;
    }

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
        // إذا الـ Infant ما بينحسبوا كمقاعد:
        return adults + children;
    }
}
