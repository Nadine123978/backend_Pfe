package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Image;
import com.itbulls.nadine.spring.springbootdemo.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByFolder(Folder folder);
    List<Image> findByFolderId(Long folderId);
}
