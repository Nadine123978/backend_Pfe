package com.itbulls.nadine.spring.springbootdemo.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itbulls.nadine.spring.springbootdemo.model.EventImage;
import com.itbulls.nadine.spring.springbootdemo.service.EventImageService;

import java.io.IOException;

import java.nio.file.Path;


@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*") // للسماح للـ frontend بالوصول
public class EventImageController {

    @Autowired
    private EventImageService imageService;

    // ✅ Get images by event
    @GetMapping("/{eventId}/images")
    public List<EventImage> getImages(@PathVariable Long eventId) {
        return imageService.getImagesByEventId(eventId);
    }

    // ✅ Upload image (you can use MultipartFile or just URL)
    @PostMapping("/{eventId}/images")
    public ResponseEntity<EventImage> uploadImage(
            @PathVariable Long eventId,
            @RequestParam("file") MultipartFile file) throws IOException {

        // احفظ الصورة على السيرفر أو استخدم cloudinary أو s3
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/events/" + eventId);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String imageUrl = "/uploads/events/" + eventId + "/" + fileName;

        EventImage saved = imageService.saveImage(eventId, imageUrl);
        return ResponseEntity.ok(saved);
    }

    // ✅ Delete image
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok().build();
    }
}
