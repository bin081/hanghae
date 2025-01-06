package io.hhplus.concertreservation.api.presentation.controller2;

import io.hhplus.concertreservation.api.presentation.dto2.SeatRequest;
import io.hhplus.concertreservation.api.presentation.dto2.SeatResponse;
import io.hhplus.concertreservation.api.service2.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation1")
public class ReservationController11 {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/queue-token1")
    public ResponseEntity<String> generateQueueToken(@RequestParam Long userId) {
        String token = reservationService.generateQueueToken(userId);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/dates1")
    public ResponseEntity<List<String>> getAvailableDates() {
        List<String> dates = reservationService.getAvailableDates();
        return ResponseEntity.ok(dates);
    }

    @GetMapping("/seats1")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@RequestParam String date) {
        List<SeatResponse> seats = reservationService.getAvailableSeats(date);
        return ResponseEntity.ok(seats);
    }

    @PostMapping("/reserve1")
    public ResponseEntity<String> reserveSeat(@RequestBody SeatRequest seatRequest) {
        boolean success = reservationService.reserveSeat(seatRequest.getUserId(), seatRequest.getSeatId(), seatRequest.getAmount());
        if (success) {
            return ResponseEntity.ok("Reservation successful");
        }
        return ResponseEntity.badRequest().body("Reservation failed");
    }
    @PostMapping("/payment1")
    public ResponseEntity<String> processPayment(@RequestParam Long userId, @RequestParam Long reservationId, @RequestParam Double amount) {
        boolean success = reservationService.processPayment(userId, reservationId, amount);
        if (success) {
            return ResponseEntity.ok("Payment successful");
        }
        return ResponseEntity.badRequest().body("Payment failed");
    }
}