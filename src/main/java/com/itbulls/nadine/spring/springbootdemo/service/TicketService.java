package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.dto.TicketSectionDTO;
import com.itbulls.nadine.spring.springbootdemo.model.TicketSection;
import com.itbulls.nadine.spring.springbootdemo.repository.TicketSectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketSectionRepository ticketSectionRepository;

    public TicketService(TicketSectionRepository ticketSectionRepository) {
        this.ticketSectionRepository = ticketSectionRepository;
    }

    public List<TicketSectionDTO> getSectionsForEvent(Long eventId) {
        List<TicketSection> sections = ticketSectionRepository.findByEventId(eventId);
        return sections.stream()
                .map(section -> new TicketSectionDTO(
                        section.getSectionName(),
                        section.getPrice(),
                        section.isSoldOut()
                ))
                .collect(Collectors.toList());
    }
}
