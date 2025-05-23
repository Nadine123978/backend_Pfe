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

    // جلب جميع الأقسام المرتبطة بفعالية معينة
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<SectionDTO>> getSectionsByEvent(@PathVariable Long eventId) {
        List<SectionDTO> sections = sectionService.getSectionDTOsByEventId(eventId);
        return ResponseEntity.ok(sections);
    }

    // جلب تفاصيل قسم معيّن حسب ID
    @GetMapping("/{sectionId}")
    public ResponseEntity<Section> getSectionById(@PathVariable Long sectionId) {
        return sectionService.getSectionById(sectionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    
    @Autowired
    private EventService eventService;
    @Autowired
    private SectionRepository sectionRepository;
    
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

}
