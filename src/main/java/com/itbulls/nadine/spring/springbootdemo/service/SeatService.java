package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.model.Section;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
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
        // افترض أنك تنشئ مثلا 10 مقاعد لكل قسم
        for (int row = 1; row <= 5; row++) {
            for (int number = 1; number <= 10; number++) {
                Seat seat = new Seat();
                seat.setRow(row);
                seat.setNumber(number);
                seat.setReserved(false);
                seat.setSection(section);
                seatRepository.save(seat);
            }
        }
    }
}

