package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Folder name is required")  // إضافة التحقق من القيمة
    private String name;

    private LocalDate createdDate = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    // === Getters and Setters ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
