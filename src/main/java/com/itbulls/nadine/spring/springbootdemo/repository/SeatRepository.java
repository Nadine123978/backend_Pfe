package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Seat;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findBySectionId(Long sectionId); // تمام

    List<Seat> findBySection_Event_Id(Long eventId); // تمام

    boolean existsByCodeAndSectionId(String code, Long sectionId);
    
    @Query("SELECT s FROM Seat s WHERE s.id IN :ids AND s.section.event.id = :eventId " +
            "AND s.reserved = false AND (s.locked = false OR s.lockedUntil <= :now)")
     List<Seat> findAvailableSeats(@Param("ids") List<Long> ids,
                                  @Param("eventId") Long eventId,
                                  @Param("now") LocalDateTime now);

    Seat findByCode(String code);

    @Query("SELECT s FROM Seat s WHERE s.section.event.id = :eventId AND s.reserved = false")
    List<Seat> findAvailableSeatsByEventId(@Param("eventId") Long eventId);

    List<Seat> findByIdInAndSection_Event_IdAndReservedFalse(List<Long> seatIds, Long eventId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id IN :ids")
    List<Seat> findSeatsWithLock(@Param("ids") List<Long> ids);


}

