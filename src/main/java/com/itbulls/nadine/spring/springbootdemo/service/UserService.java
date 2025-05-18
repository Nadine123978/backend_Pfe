package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Group;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.GroupRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;

import jakarta.transaction.Transactional;

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
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ ضروري لتشفير الباسورد

    public User createUser(User user) {
        // جلب المجموعة الافتراضية (ID = 2)
        Group defaultGroup = groupRepository.findById(2L).orElse(null);
        if (defaultGroup == null) {
            throw new RuntimeException("Default group with ID 2 not found!");
        }

        // ربط المستخدم بالمجموعة
        user.setGroup(defaultGroup);

        // 🔐 تشفير كلمة المرور قبل الحفظ
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // حفظ المستخدم في قاعدة البيانات
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        // حذف الحجوزات المرتبطة بالمستخدم أولاً
        bookingRepository.deleteByUserId(id);

        // حذف المستخدم بعد حذف الحجوزات
        userRepository.deleteById(id);
    } 
    
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
