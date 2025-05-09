package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Folder;
import com.itbulls.nadine.spring.springbootdemo.model.Image;
import com.itbulls.nadine.spring.springbootdemo.repository.FolderRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // أو حسب واجهتك
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FolderRepository folderRepository;

    // 📥 1. Get images of a folder
    @GetMapping("/folders/{folderId}/images")
    public ResponseEntity<?> getImagesByFolder(@PathVariable Long folderId) {
        Optional<Folder> folderOpt = folderRepository.findById(folderId);
        if (folderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Image> images = imageRepository.findByFolder(folderOpt.get());
        return ResponseEntity.ok(images);
    }

    // ⬆️ 2. Upload image to a folder
    @PostMapping("/folders/{folderId}/images")
    public ResponseEntity<?> uploadImage(@PathVariable Long folderId,
                                         @RequestParam("image") MultipartFile imageFile) {
        Optional<Folder> folderOpt = folderRepository.findById(folderId);
        if (folderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Image is required");
        }

        // ⚠️ dummy storage: we pretend to store the image
        // Replace this with real file storage (e.g. S3 or local)
        String fakeImageUrl = "https://dummyimage.com/600x400/000/fff&text=" + imageFile.getOriginalFilename();

        Image image = new Image();
        image.setImageUrl(fakeImageUrl); // real URL if you store images
        image.setFolder(folderOpt.get());

        imageRepository.save(image);

        return ResponseEntity.ok(image);
    }

    // ❌ 3. Delete image by ID
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        Optional<Image> imageOpt = imageRepository.findById(imageId);
        if (imageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        imageRepository.deleteById(imageId);
        return ResponseEntity.ok().build();
    }
}
