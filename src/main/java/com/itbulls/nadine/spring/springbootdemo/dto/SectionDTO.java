package com.itbulls.nadine.spring.springbootdemo.dto;

import java.util.List;

public class SectionDTO {
    private Long id;
    private String name;
    private Double price;
    private String color;
    private List<SeatDTO> seats;

    public SectionDTO() {}

    public SectionDTO(Long id, String name, Double price, String color, List<SeatDTO> seats) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.color = color;
        this.seats = seats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDTO> seats) {
        this.seats = seats;
    }
}
