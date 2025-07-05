package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "event")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventImage> images;


    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"events"})
    private Category category;


    @Enumerated(EnumType.STRING)
    private EventStatus status;
    



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Location location;


    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String imageUrl;
    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = EventStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.status == null) {
            this.status = EventStatus.DRAFT;
        }
    }


    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean published;

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Transient
    public Integer getTotalTickets() {
        if (sections == null || sections.isEmpty()) {
            return 0;
        }
        return sections.stream()
                .mapToInt(section -> section.getSeats() != null ? section.getSeats().size() : 0)
                .sum();
    }

    @Transient
    public Integer getSoldTickets() {
        if (sections == null || sections.isEmpty()) {
            return 0;
        }
        return sections.stream()
                .mapToInt(section -> section.getSeats() != null
                        ? (int) section.getSeats().stream().filter(seat -> seat.isSold()).count()
                        : 0)
                .sum();
    }

    @Transient
    public Integer getAvailableSeats() {
        return getTotalTickets() - getSoldTickets();
    }

    @Transient
    public Double getMinPrice() {
        if (sections == null || sections.isEmpty()) return 0.0;
        return sections.stream()
                .mapToDouble(section -> section.getPrice() != null ? section.getPrice() : Double.MAX_VALUE)
                .min()
                .orElse(0.0);
    }

    @Transient
    public Double getMaxPrice() {
        if (sections == null || sections.isEmpty()) return 0.0;
        return sections.stream()
                .mapToDouble(section -> section.getPrice() != null ? section.getPrice() : 0.0)
                .max()
                .orElse(0.0);
    }
}
