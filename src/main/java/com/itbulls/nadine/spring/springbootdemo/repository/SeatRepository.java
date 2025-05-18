package com.itbulls.nadine.spring.springbootdemo.repository;

import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findBySectionId(Long sectionId); // ✅ لازم تضيفها

    List<Seat> findBySection_Event_Id(Long eventId); // إذا بدك تسحب حسب الفعالية

    boolean existsByCodeAndSectionId(String code, Long sectionId);
}
