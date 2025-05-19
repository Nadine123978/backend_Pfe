package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.ContactRequest;
import com.itbulls.nadine.spring.springbootdemo.model.ContactMessage;
import com.itbulls.nadine.spring.springbootdemo.model.Notification;
import com.itbulls.nadine.spring.springbootdemo.repository.ContactMessageRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})
public class ContactController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    // مفتاح الـ secret الخاص بـ reCAPTCHA (حطه هنا أو في ملف properties)
    private final String RECAPTCHA_SECRET = "6LeBXT4rAAAAAOnxRmDH_eireQ82SgeaEyFI2Ptp";

    @PostMapping
    public ResponseEntity<?> receiveContact(@RequestBody ContactRequest contactRequest) {
        try {
            // تحقق من reCAPTCHA أولاً
            if (!verifyRecaptcha(contactRequest.getCaptchaToken())) {
                return ResponseEntity.badRequest().body("reCAPTCHA verification failed");
            }

            // إنشاء كائن ContactMessage وحفظه
            ContactMessage contactMessage = new ContactMessage();
            contactMessage.setFullName(contactRequest.getFullName());
            contactMessage.setEmail(contactRequest.getEmail());
            contactMessage.setPhone(contactRequest.getPhone());
            contactMessage.setMessage(contactRequest.getMessage());
            //contactMessage.setRead(false);

            contactMessageRepository.save(contactMessage);

            // تسجيل إشعار
            String notifMessage = "New contact from " + contactRequest.getFullName() +
                    ", Email: " + contactRequest.getEmail();
            Notification notification = new Notification();
            notification.setMessage(notifMessage);
            notification.setRead(false);
            notificationRepository.save(notification);

            return ResponseEntity.ok("Message received successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    private boolean verifyRecaptcha(String token) {
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + RECAPTCHA_SECRET + "&response=" + token;

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);

            if (response == null) return false;

            Boolean success = (Boolean) response.get("success");
            return success != null && success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<ContactMessage>> getAllContacts() {
        List<ContactMessage> contacts = contactMessageRepository.findAll();
        return ResponseEntity.ok(contacts);
    }


}
