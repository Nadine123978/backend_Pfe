package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Category;
import com.itbulls.nadine.spring.springbootdemo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Category>> getTrendingCategories() {
        Pageable top4 = PageRequest.of(0, 4); // الحصول على أول 4 فئات رائجة فقط
        List<Category> trending = categoryRepository.findTrendingCategories(top4);

        // تحديث isTrending ديناميكيًا بناءً على عدد الأحداث
        for (Category category : trending) {
            // هنا نقوم بتحديث isTrending بناءً على عدد الأحداث
            long eventCount = category.getEvents().size(); // عدد الأحداث (افترض أن هناك علاقة بين Category و Event)
            category.setTrending(eventCount > 0); // اجعل الفئة رائجة إذا كان لديها أحداث
        }

        return ResponseEntity.ok(trending);
    }

}
