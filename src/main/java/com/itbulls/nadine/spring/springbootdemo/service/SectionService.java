package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.dto.SectionDTO;
import com.itbulls.nadine.spring.springbootdemo.dto.SeatDTO;
import com.itbulls.nadine.spring.springbootdemo.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    public List<Section> getSectionsByEventId(Long eventId) {
        return sectionRepository.findByEventId(eventId);
    }

    public Optional<Section> getSectionById(Long id) {
        return sectionRepository.findById(id);
    }

    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }
    
    public List<SectionDTO> getSectionDTOsByEventId(Long eventId) {
        List<Section> sections = sectionRepository.findByEventId(eventId);
        return sections.stream().map(section -> {
            SectionDTO dto = new SectionDTO();
            dto.setId(section.getId());
            dto.setName(section.getName());
            dto.setPrice(section.getPrice());
            dto.setColor(section.getColor());

            List<SeatDTO> seatDTOs = section.getSeats().stream().map(seat -> {
                SeatDTO seatDTO = new SeatDTO();
                seatDTO.setId(seat.getId());
                seatDTO.setCode(seat.getCode());
                seatDTO.setReserved(seat.isReserved());
                return seatDTO;
            }).toList();

            dto.setSeats(seatDTOs);
            return dto;
        }).toList();
    }
    
    public List<SectionDTO> getSectionsWithSeats() {
        List<Section> sections = sectionRepository.findAll();

        return sections.stream().map(section -> {
            SectionDTO dto = new SectionDTO();
            dto.setId(section.getId());
            dto.setName(section.getName());
            dto.setColor(section.getColor());

            List<SeatDTO> seatDTOs = section.getSeats().stream().map(seat -> {
                SeatDTO seatDto = new SeatDTO();
                seatDto.setId(seat.getId());
                seatDto.setCode(seat.getCode());
               // seatDto.setPrice(seat.getPrice());
                seatDto.setReserved(seat.isReserved());
                seatDto.setColor(seat.getColor()); // 👈 ضروري جداً
                return seatDto;
            }).collect(Collectors.toList());

            dto.setSeats(seatDTOs);
            return dto;
        }).collect(Collectors.toList());
    }

    @Autowired
    private EventService eventService;
    
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    

}
