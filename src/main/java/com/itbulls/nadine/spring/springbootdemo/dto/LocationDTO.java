package com.itbulls.nadine.spring.springbootdemo.dto;

public class LocationDTO {
    private Long id;
    private String venueName;

    // Constructors
    public LocationDTO() {}

    public LocationDTO(Long id, String venueName) {
        this.id = id;
        this.venueName = venueName;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getVenueName() {
        return venueName;
    }
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }
}

