// GroupDto.java
package com.itbulls.nadine.spring.springbootdemo.dto;

import com.itbulls.nadine.spring.springbootdemo.model.Group;

public class GroupDto {
    private Long id;
    private String name;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // تحويل من Entity إلى DTO
    public static GroupDto convertToDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        return dto;
    }
}
