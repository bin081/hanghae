package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.presentation.dto.ReservationRequestDto;
import io.hhplus.concertreservation.api.presentation.dto.SeatRequest;
import io.hhplus.concertreservation.api.service.SeatReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seat/reservation")
public class SeatReservationController {

    /*좌석 예약 요청 관련된 컨트롤러*/

    private final SeatReservationService reservationService;

    @Autowired
    public SeatReservationController(SeatReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // 좌석 예약 요청 API
    @PostMapping("/request")
    public ResponseEntity<String> reserveSeat(@RequestBody SeatRequest requestDto) {
        boolean result = reservationService.reserveSeat(requestDto.getUserToken(), requestDto.getDate(), requestDto.getSeatNumber());
        if (result) {
            return ResponseEntity.ok("Reservation successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reservation failed");
        }
    }
}
