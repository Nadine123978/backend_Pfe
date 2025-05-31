package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Group;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
	 Optional<Group> findByName(String name);  // <-- هيك لازم تكونهيدا اختياري إذا بدك تبحث بالإسم
}
