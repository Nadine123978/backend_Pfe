package com.itbulls.nadine.spring.springbootdemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    
    private boolean available = true; // المقعد متاح بشكل افتراضي

    // ... باقي الخصائص مثل رقم المقعد أو رقم القاعة أو غيرها

    // Getter و Setter للـ available
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


    @ManyToOne
    @JoinColumn(name = "section_id")
    @JsonBackReference(value = "section-seat")
    private Section section;

    @OneToOne(mappedBy = "seat")
    @JsonBackReference(value = "seat-booking")
    private Booking booking;

    @Column(name = "is_reserved", nullable = false)
    private boolean reserved = false; 
    


    // Getters & Setters

    public Long getId() {
        return id;
    }
    

public boolean getReserved() {
    return reserved;
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
