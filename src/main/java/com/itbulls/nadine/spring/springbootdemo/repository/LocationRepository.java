package com.itbulls.nadine.spring.springbootdemo.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itbulls.nadine.spring.springbootdemo.model.Location;


public interface LocationRepository extends JpaRepository<Location, Long> {
	  Optional<Location> findByVenueName(String venueName);
}
