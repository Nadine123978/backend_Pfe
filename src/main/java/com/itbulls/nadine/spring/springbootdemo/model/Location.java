package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String venueName;

    private Double latitude;
    private Double longitude;

    // ✅ Constructors
    public Location() {}

    public Location(String venueName, Double latitude, Double longitude) {
        this.venueName = venueName;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
