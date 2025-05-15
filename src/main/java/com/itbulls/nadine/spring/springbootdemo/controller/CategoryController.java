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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRepository eventRepository;

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    // ✅ إرجاع أول 4 فئات رائجة (فقط الاسم والـ id بدون الأحداث)
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

    // ✅ إرجاع الأحداث حسب category ID
    @GetMapping("/{id}/events")
    public ResponseEntity<List<Event>> getEventsByCategoryId(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<Event> events = eventRepository.findByCategoryId(id);
        return ResponseEntity.ok(events);
    }
}
