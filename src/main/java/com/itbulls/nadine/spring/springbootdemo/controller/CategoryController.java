package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.CategoryDTO;
import com.itbulls.nadine.spring.springbootdemo.model.Category;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Folder;
import com.itbulls.nadine.spring.springbootdemo.repository.CategoryRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.FolderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.IOException;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private FolderRepository folderRepository;


    // إضافة تصنيف جديد (عادي)
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    // عرض كل التصنيفات
    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

 // عرض أول 4 تصنيفات رائجة
    @GetMapping("/trending")
    public ResponseEntity<List<CategoryDTO>> getTrendingCategories(
            @RequestParam(value = "limit", required = false) Integer limit) {

        List<Category> trending;

        if (limit != null && limit > 0) {
            Pageable pageable = PageRequest.of(0, limit);
            trending = categoryRepository.findByIsTrendingTrue(pageable);
        } else {
            trending = categoryRepository.findByIsTrendingTrue();
        }

        List<CategoryDTO> result = trending.stream()
            .map(category -> new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getImageUrl()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }


    // عرض الأحداث حسب التصنيف
    @GetMapping("/{id}/events")
    public ResponseEntity<List<Event>> getEventsByCategoryId(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<Event> events = eventRepository.findByCategoryId(id);
        return ResponseEntity.ok(events);
    }

    // عرض عدد التصنيفات
    @GetMapping("/count")
    public ResponseEntity<Long> getCategoryCount() {
        long count = categoryRepository.count();
        return ResponseEntity.ok(count);
    }

    
    // ✅ إضافة تصنيف مع رفع صورة
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")

    @PostMapping("/upload")
    public ResponseEntity<Category> uploadCategory(@RequestParam("name") String name,
                                                   @RequestParam("image") MultipartFile imageFile,
                                                   @RequestParam(value = "status", defaultValue = "Inactive") String status) throws IOException {
        String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path uploadPath = Paths.get("uploads", filename);
        Files.copy(imageFile.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

        Category cat = new Category();
        cat.setName(name);
        cat.setStatus(status); // ✅
        cat.setImageUrl("/uploads/" + filename);
        categoryRepository.save(cat);

        return ResponseEntity.ok(cat);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "is_trending", required = false) Boolean isTrending) throws IOException {

        Optional<Category> optional = categoryRepository.findById(id);

        if (optional.isPresent()) {
            Category cat = optional.get();
            try {
                cat.setName(name);

                if (status != null)
                    cat.setStatus(status);

                if (isTrending != null)
                    cat.setIsTrending(isTrending);

                if (imageFile != null && !imageFile.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                    Path uploadPath = Paths.get("uploads", filename);
                    Files.copy(imageFile.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
                    cat.setImageUrl("/uploads/" + filename);
                }

                Category updated = categoryRepository.save(cat);
                return ResponseEntity.ok(updated);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // حذف تصنيف
    // حذف تصنيف مع إزالة الأحداث التابعة أولاً
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Optional<Category> optional = categoryRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // أولًا: احذف الأحداث (Events) المرتبطة بهذا الصنف
        List<Event> events = eventRepository.findByCategoryId(id);
        for (Event ev : events) {
            // قبل حذف كل حدث، احذف كل الـ Folders المرتبطة به
            List<Folder> folders = folderRepository.findByEventId(ev.getId());
            if (!folders.isEmpty()) {
                folderRepository.deleteAll(folders);
            }
            // الآن احذف هذا الحدث نفسه
            eventRepository.delete(ev);
        }

        // ثم احذف الصنف (Category) نفسه
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
