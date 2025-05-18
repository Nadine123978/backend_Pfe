package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@CrossOrigin(origins = "*")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    // جلب جميع الأقسام المرتبطة بفعالية معينة
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Section>> getSectionsByEvent(@PathVariable Long eventId) {
        List<Section> sections = sectionService.getSectionsByEventId(eventId);
        return ResponseEntity.ok(sections);
    }

    // جلب تفاصيل قسم معيّن حسب ID
    @GetMapping("/{sectionId}")
    public ResponseEntity<Section> getSectionById(@PathVariable Long sectionId) {
        return sectionService.getSectionById(sectionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
