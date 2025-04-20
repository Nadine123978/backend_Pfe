package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String venueName;
    private String city;
    private String state;
    private String country;
    private String fullAddress;

    // ✅ Constructors (optional)
    public Location() {}

    public Location(String venueName, String city, String state, String country, String fullAddress) {
        this.venueName = venueName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.fullAddress = fullAddress;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}