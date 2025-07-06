package com.itbulls.nadine.spring.springbootdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import com.itbulls.nadine.spring.springbootdemo.model.Event;
import com.itbulls.nadine.spring.springbootdemo.model.EventBookingStats;
import com.itbulls.nadine.spring.springbootdemo.model.EventStatus;

public interface EventRepository extends JpaRepository<Event, Long> {
    
    // البحث حسب حالة الحدث (enum)
    List<Event> findByStatus(EventStatus status);

    long countByStatus(EventStatus status);

    List<Event> findByTitleContainingIgnoreCase(String title);

    List<Event> findByCategory_Id(Long categoryId);

    List<Event> findByCategoryId(Long categoryId);

    @Query("SELECT e FROM Event e WHERE e.status = com.itbulls.nadine.spring.springbootdemo.model.EventStatus.UPCOMING ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents();

    List<Event> findByStatusIn(List<EventStatus> statuses);

    List<Event> findByEndDateBeforeAndStatusNot(LocalDateTime date, EventStatus status);

    List<Event> findByStatusAndEndDateBefore(EventStatus status, LocalDateTime dateTime);

    @Query("SELECT e FROM Event e WHERE e.status = com.itbulls.nadine.spring.springbootdemo.model.EventStatus.UPCOMING AND e.published = true")
    List<Event> findUpcomingPublishedEvents();

    @Query("SELECT e FROM Event e WHERE e.id NOT IN (SELECT DISTINCT f.event.id FROM Folder f)")
    List<Event> findEventsWithoutFolders();
    
    @Query("SELECT new com.itbulls.nadine.spring.springbootdemo.model.EventBookingStats(e.title, COUNT(b)) " +
    	       "FROM Event e LEFT JOIN e.bookings b GROUP BY e.title")
    	List<EventBookingStats> findEventBookingStats();

    long count();
}
