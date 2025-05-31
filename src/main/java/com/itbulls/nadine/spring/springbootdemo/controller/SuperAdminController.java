package com.itbulls.nadine.spring.springbootdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.itbulls.nadine.spring.springbootdemo.dto.CreateAdminRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.UpdateUserRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.UserDto;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/secure1234/super-admin")
public class SuperAdminController {

    private final UserService userService;
  
    @Autowired
    public SuperAdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public String createAdmin(@RequestBody CreateAdminRequest request) {
        userService.createAdmin(request.getEmail(), request.getUsername(), request.getPassword(), request.getGroupId());
        return "Admin created";
    }

    @PutMapping("/update-user/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        userService.updateUser(id, request);
        return "User updated successfully";
    }

    @DeleteMapping("/delete-admin/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String deleteAdmin(@PathVariable Long id) {
        userService.deleteAdminById(id);
        return "Admin deleted successfully";
    }

    @GetMapping("/admins")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public List<UserDto> getAdmins() {
        int adminGroupId = 1;
        return userService.getUsersByGroupId(adminGroupId);
    }
}


