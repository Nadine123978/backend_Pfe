package com.itbulls.nadine.spring.springbootdemo.dto;

import com.itbulls.nadine.spring.springbootdemo.model.Group;
import com.itbulls.nadine.spring.springbootdemo.model.User;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private GroupDto group;  // هنا بدّلنا من String إلى GroupDto

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public GroupDto getGroup() { return group; }
    public void setGroup(GroupDto group) { this.group = group; }

    public static UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());

        Group group = user.getGroup();
        if (group != null) {
            GroupDto groupDto = new GroupDto();
            groupDto.setId(group.getId());
            groupDto.setName(group.getName());
            dto.setGroup(groupDto);
        }

        return dto;
    }

}
