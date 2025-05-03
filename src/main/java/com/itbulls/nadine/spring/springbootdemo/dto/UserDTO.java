package com.itbulls.nadine.spring.springbootdemo.dto;

import com.itbulls.nadine.spring.springbootdemo.model.User;
public final class UserDTO {
    private final Long id;
    private final String username;
    private final String email;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
