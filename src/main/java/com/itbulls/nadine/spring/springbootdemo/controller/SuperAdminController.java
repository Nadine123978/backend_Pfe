package com.itbulls.nadine.spring.springbootdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.itbulls.nadine.spring.springbootdemo.dto.CreateAdminRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.UpdateUserRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.UserDto;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/secure1234/super-admin")
public class SuperAdminController {

	private final UserService userService;
	private final UserRepository userRepository;

	@Autowired
	public SuperAdminController(UserService userService, UserRepository userRepository) {
	    this.userService = userService;
	    this.userRepository = userRepository;
	}

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getGroup() == null) {
            throw new RuntimeException("User has no group");
        }

        System.out.println("User group name: " + user.getGroup().getName());

        if (!user.getGroup().getName().equals("ADMIN")) {
            throw new RuntimeException("User is not an admin");
        }

        userRepository.delete(user);
        return "Admin deleted successfully";
    }



    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<UserDto> getAdmins() {
        int adminGroupId = 2;
        return userService.getUsersByGroupId(adminGroupId);
    }
}


