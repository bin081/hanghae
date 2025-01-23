package io.hhplus.concertreservation.concurrency.Controller;

import io.hhplus.concertreservation.concurrency.Service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    // 좌석 예약 요청
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveSeat(@RequestParam Integer seatNumber,
                                              @RequestParam String userId) {
        seatService.reserveSeat(Long.valueOf(seatNumber), userId);
        return ResponseEntity.ok("SeatEntityConcurrency reserved or held successfully.");
    }

    // 좌석 해제
    @PostMapping("/release/{seatId}")
    public ResponseEntity<String> releaseSeat(@PathVariable Long seatId) {
        seatService.releaseSeat(seatId);
        return ResponseEntity.ok("SeatEntityConcurrency released successfully.");
    }
}
