package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

    // Getters and setters
    public Long getId() { return id; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Folder getFolder() { return folder; }

    public void setFolder(Folder folder) { this.folder = folder; }
}
