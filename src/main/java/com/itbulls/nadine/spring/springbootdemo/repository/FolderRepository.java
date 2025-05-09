package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}