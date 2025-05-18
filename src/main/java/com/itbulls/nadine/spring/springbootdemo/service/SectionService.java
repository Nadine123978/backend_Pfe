package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
