package com.itbulls.nadine.spring.springbootdemo.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class PasswordResetRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Constructor
    public PasswordResetRequest(String email) {
        this.email = email;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
