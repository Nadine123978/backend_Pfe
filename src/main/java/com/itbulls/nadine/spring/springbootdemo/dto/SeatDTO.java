package com.itbulls.nadine.spring.springbootdemo.dto;

public class SeatDTO {
    private Long id;
    private String code;
    private boolean reserved;

    // إضافة خاصية requestedSeats
    private int requestedSeats;

    public SeatDTO() {}

    public SeatDTO(Long id, String code, boolean reserved, int requestedSeats) {
        this.id = id;
        this.code = code;
        this.reserved = reserved;
        this.requestedSeats = requestedSeats;
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
}
