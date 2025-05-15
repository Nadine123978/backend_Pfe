package com.itbulls.nadine.spring.springbootdemo.dto;

public class TicketSectionDTO {
    private String sectionName;
    private double price;
    private boolean soldOut;

    public TicketSectionDTO() {}

    public TicketSectionDTO(String sectionName, double price, boolean soldOut) {
        this.sectionName = sectionName;
        this.price = price;
        this.soldOut = soldOut;
    }

    // Getters and setters
    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }
}
