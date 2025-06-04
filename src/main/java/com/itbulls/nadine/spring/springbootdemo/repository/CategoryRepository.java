package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	@Query("SELECT c FROM Category c LEFT JOIN c.events e GROUP BY c.id ORDER BY COUNT(e.id) DESC")
	List<Category> findTrendingCategories(Pageable pageable);


    long count();
}
