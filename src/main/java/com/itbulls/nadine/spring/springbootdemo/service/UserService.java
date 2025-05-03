package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Group;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.GroupRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    public User createUser(User user) {
        // جلب المجموعة بالـ ID = 2 مباشرة
        Group defaultGroup = groupRepository.findById(2L).orElse(null);

        if (defaultGroup == null) {
            throw new RuntimeException("Default group with ID 2 not found!");
        }

        // ربط المستخدم بهيدي المجموعة
        user.setGroup(defaultGroup);

        // حفظ المستخدم
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}