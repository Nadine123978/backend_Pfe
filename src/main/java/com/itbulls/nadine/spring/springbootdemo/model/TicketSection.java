package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;

@Entity
public class TicketSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sectionName;
    private double price;
    private boolean soldOut;

    @ManyToOne
    @JoinColumn(name = "event_id") // اسم العمود في قاعدة البيانات
    private Event event;

    // Constructors
    public TicketSection() {}

    public TicketSection(String sectionName, double price, boolean soldOut, Event event) {
        this.sectionName = sectionName;
        this.price = price;
        this.soldOut = soldOut;
        this.event = event;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
