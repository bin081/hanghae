package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.data.entity.UserQueue;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import io.hhplus.concertreservation.api.presentation.dto.BalanceResponse;
import io.hhplus.concertreservation.api.presentation.dto.PaymentRequest;
import io.hhplus.concertreservation.api.presentation.dto.SeatRequest;
import io.hhplus.concertreservation.api.presentation.dto.SeatResponse;
import io.hhplus.concertreservation.api.service.*;
import io.hhplus.concertreservation.api.support.exception.ErrorResponse;
import io.hhplus.concertreservation.api.support.exception.UserNotFoundException;
import io.hhplus.concertreservation.api.support.util.TokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/reservation1")
public class ReservationTotalController {

    private final UserQueueRepository userQueueRepository;


    private ReservationService reservationService;
    private final PaymentService paymentService;
    private final TokenService tokenService;
    private final QueueService queueService;
    private final SeatReservationService seatReservationService;
    private final BalanceService balanceService;

    public ReservationTotalController(ReservationService reservationService, PaymentService paymentService, TokenService tokenService, QueueService queueService, UserQueueRepository userQueueRepository, SeatReservationService seatReservationService, BalanceService balanceService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.tokenService = tokenService;
        this.queueService = queueService;
        this.seatReservationService = seatReservationService;
        this.balanceService = balanceService;
        this.userQueueRepository = userQueueRepository;
    }

    /*토큰을 생성함*/
    @PostMapping("/generate/token")
    public ResponseEntity<UserQueue> generateQueueToken(@RequestParam Long userId) {
        // 토큰 서비스 호출
        String token = tokenService.generateToken(userId);
        // 대기열 관리 서비스 호출(유저 대기열에 추가)
        UserQueue userQueue = queueService.addUserToQueue(token);
        // 대기열 관리 서비스 호출(대기열 상태 조회), 토큰 서비스 호출(토큰 검증)
        boolean result1 = queueService.isUserEligible(Optional.ofNullable(userQueue));
        if (result1 != true){
            throw new RuntimeException("대기열에 있지 않습니다.");
        } else
        return ResponseEntity.ok(userQueue);
    }

    /*토큰을 검증함*/
    @GetMapping("/validate/token")
    public ResponseEntity<Boolean> validateQueueToken(@RequestParam String token){
        // 대기열 관리 서비스 호출(대기열 상태 조회), 토큰 서비스 호출(토큰 검증)
        boolean result1 = queueService.isUserEligibleToken(token);
        if (result1 != true){
            throw new RuntimeException("대기열에 있지 않습니다.");
        } else
            return ResponseEntity.ok(true);
    }

    /*예약가능한 날짜 조회*/
    @GetMapping("/dates")
    public ResponseEntity<List<String>> getAvailableDates() {
        List<String> dates = reservationService.getAvailableDates();
        return ResponseEntity.ok(dates);
    }

    /*특정날짜에 예약가능한 좌석 조회*/
    @GetMapping("/seats")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@RequestParam String date) {
        List<SeatResponse> seats = reservationService.getAvailableSeats(date);
        return ResponseEntity.ok(seats);
    }

    /*좌석을 예약함*/
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveSeat(@RequestBody SeatRequest seatRequest) {
        boolean success = reservationService.reserveSeat(seatRequest.getUserId(), seatRequest.getSeatId(), seatRequest.getAmount());
        if (success) {
            return ResponseEntity.ok("Reservation successful");
        }
        return ResponseEntity.badRequest().body("Reservation failed");
    }

    /*잔액 충전함*/
    @PostMapping("/charge/amount")
    public ResponseEntity<String> processPayment(@RequestParam Long userId, @RequestParam Long reservationId, @RequestParam Double amount) {
        boolean success = reservationService.processPayment(userId, reservationId, amount);
        if (success) {
            return ResponseEntity.ok("Payment successful");
        }
        return ResponseEntity.badRequest().body("Payment failed");
    }

    /*잔액 조회함*/
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestHeader("Authorization") String token) {
        try {
            Long userId = TokenUtil.getUserIdFromToken(token); // JWT로부터 Users ID 추출
            BalanceResponse response = balanceService.getUserBalance(userId);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Users not found", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /* 결제 요청 처리*/
    @PostMapping("/payment")
    public ResponseEntity<String> processPayment(
            @RequestBody PaymentRequest request
    ) {
        try {
            paymentService.processPayment(request);
            return ResponseEntity.ok("결제 성공 및 좌석 예약 확정");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}