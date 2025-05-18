package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "is_trending")
    private Boolean isTrending = false; // ✅ قيمة افتراضية لتجنّب null

    @OneToMany(mappedBy = "category")
    @JsonManagedReference
    private List<Event> events;

    // Getters & Setters

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

    public Boolean getIsTrending() {
        return isTrending;
    }

    public void setIsTrending(Boolean isTrending) {
        this.isTrending = isTrending;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
