package com.itbulls.nadine.spring.springbootdemo.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Service;

@Service
public class GalleryService {

    public void saveGalleryImage(MultipartFile image, String uploadDir) throws IOException {
        // التأكد من أن اسم الملف ليس null
        String originalFilename = Objects.requireNonNull(image.getOriginalFilename(), "File name cannot be null");

        // تحويل الصورة إلى Path وتخزينها
        Path uploadPath = Paths.get(uploadDir);

        // التأكد من أن المجلد الوجهة موجود، وإذا لم يكن موجودًا يتم إنشاؤه
        Files.createDirectories(uploadPath);

        // تحديد المسار الكامل لحفظ الملف
        Path filePath = uploadPath.resolve(originalFilename);

        // نقل الملف من الذاكرة إلى المسار المحدد
        try {
            image.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new IOException("Failed to store file", e);
        }
    }
}

