package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Group;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;
import com.itbulls.nadine.spring.springbootdemo.service.UserService;
import com.itbulls.nadine.spring.springbootdemo.dto.LoginRequest;
import com.itbulls.nadine.spring.springbootdemo.dto.UserDTO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // المفتاح السري للـ JWT يتم قراءته من application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return new UserDTO(user);
    }


    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty() || !request.getPassword().equals(optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("message", "Invalid credentials"));
        }


        User user = optionalUser.get();

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());

        String groupName = Optional.ofNullable(user.getGroup())
                                   .map(Group::getName)
                                   .orElse("user");
        response.put("group", groupName);

        String jwtToken = generateJwtToken(user);
        response.put("jwt", jwtToken);

        return ResponseEntity.ok(response);
    }

    // توليد JWT حقيقي باستخدام JJWT
    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("group", user.getGroup() != null ? user.getGroup().getName() : "user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // صلاحية 24 ساعة
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
