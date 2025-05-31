package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.CategoryDTO;
import com.itbulls.nadine.spring.springbootdemo.model.Category;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.repository.CategoryRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;

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
    public ResponseEntity<List<CategoryDTO>> getTrendingCategories() {
        Pageable top4 = PageRequest.of(0, 4);
        List<Category> trending = categoryRepository.findTrendingCategories(top4);

        List<CategoryDTO> result = trending.stream()
                .filter(category -> !category.getEvents().isEmpty())
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
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
    @PostMapping("/upload")
    public ResponseEntity<Category> uploadCategory(@RequestParam("name") String name,
                                                   @RequestParam("image") MultipartFile imageFile) throws java.io.IOException {
        String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path uploadPath = Paths.get("uploads", filename);
        Files.copy(imageFile.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

        Category cat = new Category();
        cat.setName(name);
        cat.setImageUrl("/images/" + filename); // هذا المسار يُخزن في قاعدة البيانات

        categoryRepository.save(cat);
        return ResponseEntity.ok(cat);
    }
}
