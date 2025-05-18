package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private boolean reserved;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;
    
    @OneToOne(mappedBy = "seat")
    private Booking booking;
    
    @Column(name = "is_reserved", nullable = false)
    private boolean isReserved = false; // ✅ لازم يكون في قيمة ابتدائية


    // Getters & Setters

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

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
