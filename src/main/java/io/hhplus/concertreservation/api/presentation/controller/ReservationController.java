package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.presentation.dto.SeatRequest;
import io.hhplus.concertreservation.api.presentation.dto.SeatResponse;
import io.hhplus.concertreservation.api.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

   /* 예약 가능 날짜와 좌석 정보를 조회하는 API 앤드포인트*/
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/dates")
    public List<String> getAvailableDates(){
        return reservationService.getAvailableDates();
    }

    @GetMapping("/seats")
    public List<SeatResponse> getAvailableSeats(@RequestParam String date){
        return reservationService.getAvailableSeats(date);
    }
}
