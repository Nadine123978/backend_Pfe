package com.itbulls.nadine.spring.springbootdemo.exception;

public class SeatsAlreadyReservedException extends RuntimeException {
    public SeatsAlreadyReservedException(String message) {
        super(message);
    }
}