package com.itbulls.nadine.spring.springbootdemo.model;

import org.springframework.web.multipart.MultipartFile;

public class GalleryRequest {

    private MultipartFile image;
    private String caption;
    private Long eventId;

    // Getters and Setters
    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    // ✅ New method to retrieve the image file name (URL placeholder)
    public String getImageUrl() {
        return image != null ? image.getOriginalFilename() : null;
    }
}
