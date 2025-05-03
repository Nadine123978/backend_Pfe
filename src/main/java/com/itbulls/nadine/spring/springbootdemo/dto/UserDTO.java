package com.itbulls.nadine.spring.springbootdemo.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.itbulls.nadine.spring.springbootdemo.model.User;

public final class UserDTO {
    private final Long id;
    private final String username;
    private final String email;
    private String jwt; // Add jwt field

    public UserDTO(User user) {
        if (user.getUsername() == null || user.getEmail() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incomplete user data");
        }
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    // Getter methods
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // Setter for JWT token
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    // Getter for JWT token (optional)
    public String getJwt() {
        return jwt;
    }
}
