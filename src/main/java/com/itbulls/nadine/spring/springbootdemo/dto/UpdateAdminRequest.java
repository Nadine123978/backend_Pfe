package com.itbulls.nadine.spring.springbootdemo.dto;

public class UpdateAdminRequest {

    private String fullName;
    private String email;
    private String role;
    private String password;

    public UpdateAdminRequest() {}

    // Getters & Setters

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
