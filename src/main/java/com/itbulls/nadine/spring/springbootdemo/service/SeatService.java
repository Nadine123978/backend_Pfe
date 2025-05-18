package com.itbulls.nadine.spring.springbootdemo.service;

import com.itbulls.nadine.spring.springbootdemo.model.Seat;
import com.itbulls.nadine.spring.springbootdemo.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

	@Autowired
	private SeatRepository seatRepository;

    public List<Seat> getSeatsBySection(Long sectionId) {
        return seatRepository.findBySectionId(sectionId);
    }

    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    public Seat saveSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public boolean isSeatReserved(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isPresent()) {
            return optionalSeat.get().isReserved();
        } else {
            return true; // اعتبره محجوز إذا غير موجود
        }
    }
    public void markSeatAsReserved(Seat seat) {
        seat.setReserved(true);
        seatRepository.save(seat);
    }

    public void markSeatAsAvailable(Seat seat) {
        seat.setReserved(false);
        seatRepository.save(seat);
    }
    
    public Seat save(Seat seat) {
        return seatRepository.save(seat);
    }

}
