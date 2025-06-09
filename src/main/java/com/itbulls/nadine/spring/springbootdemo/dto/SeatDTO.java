package com.itbulls.nadine.spring.springbootdemo.dto;

public class SeatDTO {
    private Long id;
    private String code;
    private boolean reserved;
    private int requestedSeats;

    // ✅ الحقل الجديد لإرجاع اللون
    private String color;
    
    // أضف هذين الحقلين:
    private Integer row;
    private Integer number;

    // أضف الـ getters و setters لهما
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

    public SeatDTO() {}

    public SeatDTO(Long id, String code, boolean reserved, int requestedSeats, String color) {
        this.id = id;
        this.code = code;
        this.reserved = reserved;
        this.requestedSeats = requestedSeats;
        this.color = color;
    }

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

    public int getRequestedSeats() {
        return requestedSeats;
    }

    public void setRequestedSeats(int requestedSeats) {
        this.requestedSeats = requestedSeats;
    }

    // ✅ Getter و Setter للـ color
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
