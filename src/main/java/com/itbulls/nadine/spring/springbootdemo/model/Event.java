package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "event") // اسم الجدول حسب الصورة
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long categoryid;
    private String eventname;
    private String description;
    private Long locationid;
    private String eventdate;
    private Integer totaltickets;
    private Integer soldtickets;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Long categoryid) {
        this.categoryid = categoryid;
    }

    public String getEventname() {
        return eventname;
    }

    public void setEventname(String eventname) {
        this.eventname = eventname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLocationid() {
        return locationid;
    }

    public void setLocationid(Long locationid) {
        this.locationid = locationid;
    }

    public String getEventdate() {
        return eventdate;
    }

    public void setEventdate(String eventdate) {
        this.eventdate = eventdate;
    }

    public Integer getTotaltickets() {
        return totaltickets;
    }

    public void setTotaltickets(Integer totaltickets) {
        this.totaltickets = totaltickets;
    }

    public Integer getSoldtickets() {
        return soldtickets;
    }

    public void setSoldtickets(Integer soldtickets) {
        this.soldtickets = soldtickets;
    }
}