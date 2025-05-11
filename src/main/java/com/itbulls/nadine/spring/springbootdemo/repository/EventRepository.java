package com.itbulls.nadine.spring.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import com.itbulls.nadine.spring.springbootdemo.model.Event;



public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Add this method
	List<Event> findByStatusIgnoreCase(String status);
	  long countByStatus(String status);
	  
	  List<Event> findByTitleContainingIgnoreCase(String title); // 🔍 هاي اللي ناقصة
	  List<Event> findByIsFeaturedTrue();
	  List<Event> findByCategory_Id(Long categoryId);
	  
	   @Query("SELECT e FROM Event e WHERE LOWER(e.status) IN :statuses")
	    List<Event> findByStatusInIgnoreCase(List<String> statuses);
	   
	   @Query("SELECT e FROM Event e WHERE e.id NOT IN (SELECT DISTINCT f.event.id FROM Folder f)")
	   List<Event> findEventsWithoutFolders();


}