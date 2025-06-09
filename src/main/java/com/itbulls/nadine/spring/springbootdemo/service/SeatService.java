package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public Optional<Seat> getSeatById(Long id) {
        Optional<Seat> seatOpt = seatRepository.findById(id);
        seatOpt.ifPresent(Seat::updateLockStatus);
        return seatOpt;
    }

    public List<Seat> getAllSeats() {
        List<Seat> seats = seatRepository.findAll();
        seats.forEach(Seat::updateLockStatus);
        return seats;
    }

    public Seat save(Seat seat) {
        return seatRepository.save(seat);
    }

    public void delete(Seat seat) {
        seatRepository.delete(seat);
    }

    public List<Seat> getSeatsBySection(Long sectionId) {
        return seatRepository.findBySectionId(sectionId);
    }

    public boolean isSeatReserved(Long seatId) {
        Optional<Seat> seat = seatRepository.findById(seatId);
        return seat.map(Seat::isReserved).orElse(false);
    }

    public void generateSeatsForSection(Section section) {
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= 5; row++) {
            for (int number = 1; number <= 10; number++) {
                Seat seat = new Seat();
                seat.setRow(row);
                seat.setNumber(number);
                seat.setReserved(false);
                seat.setSection(section);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }
    
    public void markSeatAsReserved(Seat seat) {
        seat.setReserved(true);
        seatRepository.save(seat);
    }

    public void markSeatAsAvailable(Seat seat) {
        seat.setReserved(false);
        seatRepository.save(seat);
    }

    public void saveAll(List<Seat> seats) {
        seatRepository.saveAll(seats);
    }

}

