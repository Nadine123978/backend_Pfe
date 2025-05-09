package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Folder;
import com.itbulls.nadine.spring.springbootdemo.model.Image;
import com.itbulls.nadine.spring.springbootdemo.repository.ImageRepository;
import com.itbulls.nadine.spring.springbootdemo.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FolderRepository folderRepository;

    public List<Image> getImagesByFolderId(Long folderId) {
        return imageRepository.findByFolderId(folderId);
    }

    public Image addImage(Long folderId, String imageUrl) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        Image image = new Image();
        image.setFolder(folder);
        image.setImageUrl(imageUrl);
        return imageRepository.save(image);
    }

    public void deleteImage(Long imageId) {
        imageRepository.deleteById(imageId);
    }
}
