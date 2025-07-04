package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.dto.UpdateUserRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.UserDto;
import com.itbulls.nadine.spring.springbootdemo.model.Group;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.BookingRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.GroupRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    private PasswordEncoder passwordEncoder;
    



    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
// ✅ ضروري لتشفير الباسورد

    public User createUser(User user) {
        Group group = groupRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        user.setGroup(group);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

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

    public void createAdmin(String email, String username, String password, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group with id " + groupId + " not found"));

        User user = new User(username, email, passwordEncoder.encode(password), group);
        userRepository.save(user);
    }

    
    public void updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getGroupId() != null) {
            Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
            user.setGroup(group);
        }

        userRepository.save(user);
    }

    public List<UserDto> getUsersByGroupId(int groupId) {
        return userRepository.findByGroupId(groupId)
            .stream()
            .map(UserDto::convertToDto) // ✅ استدعاء الدالة statically من UserDto
            .collect(Collectors.toList());
    }


public void deleteAdminById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

    // تحقق إنو المستخدم فعلاً Admin
    if (!user.getGroup().getName().equals("admin")) {
        throw new RuntimeException("Cannot delete: User is not an Admin");
    }

    userRepository.deleteById(id);
}

}