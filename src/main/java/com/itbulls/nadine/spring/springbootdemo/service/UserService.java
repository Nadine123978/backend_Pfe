package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Group;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.GroupRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        Group defaultGroup = groupRepository.findById(2L).orElse(null);
        if (defaultGroup == null) {
            throw new RuntimeException("Default group with ID 2 not found!");
        }

        user.setGroup(defaultGroup);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // ← تشفير الباسورد
        
        return userRepository.save(user);
    }

}
