package com.itbulls.nadine.spring.springbootdemo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;  // سيتم توليده تلقائيًا من row و number

    @Column(name = "price")
    private Double price;

    @Column(name = "is_reserved", nullable = false)
    private boolean reserved = false;

    @Column(name = "seat_color")
    private String color;

    @Column(name = "seat_row")
    private Integer row;

    @Column(name = "seat_number")
    private Integer number;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @JsonBackReference(value = "section-seat")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @JsonBackReference(value = "seat-booking")
    private Booking booking;

    @Column(name = "locked")
    private Boolean locked = false;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    // ==============================
    // Logic
    // ==============================

    public boolean isAvailable() {
        return !isSold();
    }

    public boolean isSold() {
        return reserved || (booking != null);
    }

    public boolean isLocked() {
        updateLockStatus();
        return Boolean.TRUE.equals(locked);
    }

    public void updateLockStatus() {
        if (locked && lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil)) {
            locked = false;
            lockedUntil = null;
        }
    }

    // دالة توليد الكود التلقائي
    private void generateCode() {
        if (row != null && number != null) {
            this.code = row + "-" + number;
        }
    }

    // ==============================
    // Setters with logic
    // ==============================

    public void setRow(Integer row) {
        this.row = row;
        generateCode();
    }

    public void setNumber(Integer number) {
        this.number = number;
        generateCode();
    }

    // ==============================
    // باقي الـ Getters & Setters
    // ==============================

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getNumber() {
        return number;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}
