package com.itbulls.nadine.spring.springbootdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itbulls.nadine.spring.springbootdemo.model.Location;
import com.itbulls.nadine.spring.springbootdemo.repository.LocationRepository;



@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*") // لو عم تشتغلي محلي على localhost:3000
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;

    @PostMapping
    public ResponseEntity<?> addLocation(@RequestBody Location location) {
        if (locationRepository.findByVenueName(location.getVenueName()).isPresent()) {
            return ResponseEntity
                     .badRequest()
                     .body("اسم المكان موجود مسبقاً، الرجاء اختيار اسم آخر");
        }
        Location savedLocation = locationRepository.save(location);
        return ResponseEntity.ok(savedLocation);
    }


    // (اختياري) تجيب كل المواقع
    @GetMapping
    public ResponseEntity<?> getAllLocations() {
        return ResponseEntity.ok(locationRepository.findAll());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        if (!locationRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Location not found");
        }
        locationRepository.deleteById(id);
        return ResponseEntity.ok("Location deleted successfully");
    }



}
