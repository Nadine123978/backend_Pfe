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

    @Column(unique = true)  // <--- هنا
    private String code;

   
    public boolean isAvailable() {
        return !isSold();
    }


    @Column(name = "price")
    private Double price;  // السعر

    @Column(name = "is_reserved", nullable = false)
    private boolean reserved = false; 

    @Column(name = "seat_color")   // أضفت خاصية اللون هنا
    private String color;

    @ManyToOne
    @JoinColumn(name = "section_id")
    @JsonBackReference(value = "section-seat")
    private Section section;

    @OneToOne(mappedBy = "seat")
    @JsonBackReference(value = "seat-booking")
    private Booking booking;

    @Column(name = "seat_row")  // بدل row
    private Integer row;

    @Column(name = "seat_number")  // بدل number
    private Integer number;
    
    // تأكد من وجود علاقة مع Event



    // Getters and setters

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


    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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
    
    public boolean isSold() {
        // مثال: كرسي يعتبر "مباع" إذا كان محجوز (reserved) أو إذا مرتبط بحجز (booking) غير فارغ
        return reserved || (booking != null);
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
    
    @Column(name = "locked")
    private Boolean locked = false;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    public boolean isLocked() {
        return locked != null ? locked : false;
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

    // دالة منفصلة لتحديث القفل
    public void updateLockStatus() {
        if (locked && lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil)) {
            locked = false;
            lockedUntil = null;
        }
    }


}
