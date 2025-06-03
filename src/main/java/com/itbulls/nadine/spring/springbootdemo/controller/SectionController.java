package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.repository.SectionRepository;
import com.itbulls.nadine.spring.springbootdemo.dto.SectionDTO;
import com.itbulls.nadine.spring.springbootdemo.service.EventService;
import com.itbulls.nadine.spring.springbootdemo.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/sections")
@CrossOrigin(origins = "http://localhost:5173")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private EventService eventService;

    @Autowired
    private SectionRepository sectionRepository;

    @GetMapping
    public ResponseEntity<List<Section>> getAllSections() {
        List<Section> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<SectionDTO>> getSectionsByEvent(@PathVariable Long eventId) {
        List<SectionDTO> sections = sectionService.getSectionDTOsByEventId(eventId);
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<Section> getSectionById(@PathVariable Long sectionId) {
        return sectionService.getSectionById(sectionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/with-seats")
    public ResponseEntity<List<SectionDTO>> getSectionsWithSeats() {
        List<SectionDTO> sections = sectionService.getSectionsWithSeats();
        return ResponseEntity.ok(sections);
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Section> createSectionForEvent(
            @RequestParam("eventId") Long eventId,
            @RequestBody Section section
    ) {
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            throw new RuntimeException("Event not found");
        }

        section.setEvent(event);
        Section saved = sectionRepository.save(section);
        return ResponseEntity.ok(saved);
    }

    // تعديل قسم موجود
    @PutMapping("/{sectionId}")
    public ResponseEntity<Section> updateSection(
            @PathVariable Long sectionId,
            @RequestBody Section updatedSection
    ) {
        return sectionRepository.findById(sectionId)
                .map(section -> {
                    section.setName(updatedSection.getName());
                    section.setColor(updatedSection.getColor());
                    section.setTotalSeats(updatedSection.getTotalSeats());
                    section.setPrice(updatedSection.getPrice());
                    // إذا بدك تحدث العلاقة مع Event أيضًا، لازم تضيف هنا
                    Section saved = sectionRepository.save(section);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // حذف قسم حسب الـ ID
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        return sectionRepository.findById(sectionId)
                .map(section -> {
                    sectionRepository.delete(section);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
