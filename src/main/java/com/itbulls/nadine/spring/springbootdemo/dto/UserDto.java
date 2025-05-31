package com.itbulls.nadine.spring.springbootdemo.dto;

import com.itbulls.nadine.spring.springbootdemo.model.User;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String groupName;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    

    public static UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setGroupName(user.getGroup().getName()); // ✅ صححنا هذا
        return dto;
    }

}
