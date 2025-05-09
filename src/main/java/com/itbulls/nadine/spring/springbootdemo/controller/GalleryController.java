package com.itbulls.nadine.spring.springbootdemo.controller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Gallery;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.GalleryRepository;

import java.util.Objects;




@RestController
@RequestMapping("/api/gallery")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})

    public class GalleryController {
    	
        @Autowired
        private GalleryRepository galleryRepository;

        @Autowired
        private EventRepository eventRepository;

        @Value("${upload.dir}")
        private String uploadDir; // تأكد من تعريف uploadDir في application.properties

        @PostMapping("/upload")
        public ResponseEntity<?> uploadGallery(
                @RequestParam("images") MultipartFile[] images,
                @RequestParam("caption") String caption,
                @RequestParam("eventId") Long eventId
        ) {
            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile image : images) {
                if (image.isEmpty()) continue;

                String originalFilename = Objects.requireNonNull(image.getOriginalFilename());
                String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

                try {
                    // تحديد مسار التخزين باستخدام uploadDir
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // إنشاء المسار الكامل للملف
                    Path filePath = (uploadPath).resolve(uniqueFilename);
                    
                    // نسخ الصورة إلى المسار
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // إضافة الرابط إلى القائمة
                    String imageUrl = "/uploads/" + uniqueFilename;
                    imageUrls.add(imageUrl);

                    // هنا يمكنك إضافة الكود لتخزين البيانات في قاعدة البيانات
                    // مثل: create new Gallery entity with imageUrl, caption, eventId

                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(500).body("❌ Error uploading " + originalFilename);
                }
            }

            // إرجاع رسالة النجاح مع الروابط المحفوظة
            return ResponseEntity.ok().body(Map.of(
                    "message", "✅ Uploaded successfully",
                    "urls", imageUrls
            ));
        }
    
    // استرجاع كل الصور المرتبطة بحدث
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Gallery>> getGalleryByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(galleryRepository.findByEventId(eventId));
    }
}
