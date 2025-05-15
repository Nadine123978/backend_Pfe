package com.itbulls.nadine.spring.springbootdemo.controller;

import com.itbulls.nadine.spring.springbootdemo.dto.TicketSectionDTO;
import com.itbulls.nadine.spring.springbootdemo.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<TicketSectionDTO>> getTicketSections(@PathVariable Long id) {
        List<TicketSectionDTO> sections = ticketService.getSectionsForEvent(id);
        return ResponseEntity.ok(sections);
    }
}