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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    
    private User validateResetToken(String resetToken) {
        // تحقق من الرمز في قاعدة البيانات أو في التوكين الذي تم توليده مسبقاً
        // في هذه الحالة، نستخدم UUID عشوائي، ولكن يمكن تخصيص هذه الطريقة بناءً على نظامك

        // مثال: استخدام UUID في قاعدة البيانات للتأكد من أن الرمز صالح
        Optional<User> userOptional = userRepository.findByResetToken(resetToken);

        return userOptional.orElse(null); // إذا لم نجد المستخدم، نعيد null
    }
    
    @Value("${jwt.secret}")
    private String secretKey;
 
    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("group", user.getGroup() != null ? user.getGroup().getName() : "user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // صلاحية 24 ساعة
                .signWith(SignatureAlgorithm.HS256, secretKey) // استخدم الـ secretKey الذي قرأته من التطبيق
                .compact();
    }


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        // Check if the user exists and verify the password
        if (optionalUser.isEmpty() || !passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid credentials");
        }

        User user = optionalUser.get();
        
        // Generate JWT token (optional)
        String jwtToken = generateJwtToken(user);
        
        // Create UserDTO and add JWT token to the response
        UserDTO userDTO = new UserDTO(user);
        userDTO.setJwt(jwtToken); // Add the JWT token to the response

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        System.out.println("Received ID: " + id); // Log ID to see if it's correctly passed
        User user = userService.getUserById(id);
        System.out.println("User fetched: " + user); // debug
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


    
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestParam String resetToken, @RequestParam String newPassword) {
        // تحقق من صحة الرمز
        User user = validateResetToken(resetToken);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token.");
        }

        // تشفير كلمة المرور الجديدة
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);

        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully.");
    }

}
