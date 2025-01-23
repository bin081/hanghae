package io.hhplus.concertreservation.mockapi.controller;

import io.hhplus.concertreservation.mockapi.model.ApiResponse;
import io.hhplus.concertreservation.mockapi.model.CashResponse;
import io.hhplus.concertreservation.mockapi.model.ReservationResponse;
import io.hhplus.concertreservation.mockapi.model.TokenResponse;
import io.hhplus.concertreservation.mockapi.service.MockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/mock")
public class MockController {

    @Autowired
    private MockManager mockManager;


    //대기열 추가 : 토큰이 있는지 없는지 확인하여 대기열에 추가 또는 순번을 부여해줌
    @PostMapping("/queue")
    public ApiResponse<TokenResponse> insertQueue(HttpServletRequest request){

        TokenResponse response = new TokenResponse();
        // Authorization 헤더에서 토큰을 가져옵니다.
        String headerToken = request.getHeader("Authorization");

        if (headerToken == null) {
            // 1. 처음 접속한 사용자인지 체크
            long userId = 123L;  // 유저 ID는 실제로 UUID 또는 DB에서 가져올 수 있습니다.
            mockManager.insertWaitList(userId);  // 대기열에 유저를 추가

            // 새로 발급된 토큰과 대기 시간을 포함하여 응답
            response = new TokenResponse(userId, generateNewToken(), 10L);  // 10L은 대기 시간 예시 (초 단위)
        } else {
            // 2. 이미 접속한 사용자 인지 체크
            long userId = 11L;  // 유저 ID는 DB나 세션에서 확인할 수 있습니다.
            mockManager.insertWaitList(userId);  // 대기열에 유저를 추가

            // 기존 토큰과 대기 시간을 포함하여 응답
            response = new TokenResponse(userId, headerToken, 2L);  // 2L은 남은 대기 시간 예시 (초 단위)
        }

        // 성공적인 API 호출에 대한 응답 반환
        return new ApiResponse<TokenResponse>(response);
    }

    // 새 토큰을 생성하는 예시 메소드
    private String generateNewToken() {
        // 예시로 UUID를 생성하여 토큰으로 사용
        return UUID.randomUUID().toString();
    }

    // 예약 가능 일자/ 좌석 조회
    @GetMapping("/available")
    public ApiResponse<ReservationResponse> selectAvailableDateAndSeat(HttpServletRequest request, @RequestParam Long userId) {
        request.getHeader("Authorization");
        ReservationResponse response = new ReservationResponse("2025-01-01, 2025-01-02", "1, 2, 3, 4, 5");
        return new ApiResponse<ReservationResponse>(response);
    }

    // 특정 일자 신청 가능한 좌석 조회
    @GetMapping("/specific/available")
    public ApiResponse<ReservationResponse> selectSpecailDateAndSeat(HttpServletRequest request, @RequestParam LocalDate date) {
        ReservationResponse response = new ReservationResponse("2025-01-01", "1, 2, 3, 4, 5");
        return new ApiResponse<ReservationResponse>(response);
    }


    // 좌석 예약 요청 API
    @PostMapping("/reserve")
    public ApiResponse<ReservationResponse> getRegister(@RequestParam Long userId, @RequestParam Integer seatNumber) {
        ReservationResponse response = new ReservationResponse("2024-01-01", "SeatEntityConcurrency " + seatNumber);
        return new ApiResponse<ReservationResponse>(response);
    }

    // 잔액 충전 API
    @PostMapping("/cash/charge")
    public ApiResponse<CashResponse> chargeCash(@RequestParam Long userId, @RequestParam Double amount) {
        if (amount <= 0) {
            return new ApiResponse<>("error", "Amount must be greater than 0", null);
        }
        
        mockManager.chargeCash(userId, amount);  // 충전된 금액 반영
        CashResponse response = new CashResponse(123L, 10000.0);
        double totalAmount = response.getAmount() + amount;
        response = new CashResponse(123L, totalAmount);
        return new ApiResponse<CashResponse>(response);
    }

    // 잔액 조회 API
    @GetMapping("/cash/select")
    public ApiResponse<CashResponse> selectCash(@RequestParam Long userId) {
        Double balance = mockManager.getBalance(userId);  // 사용자의 잔액 조회
        CashResponse response = new CashResponse(123L, 10000.0);
        return new ApiResponse<CashResponse>(response);
    }

}
