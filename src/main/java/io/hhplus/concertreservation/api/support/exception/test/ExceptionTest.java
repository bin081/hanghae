package io.hhplus.concertreservation.api.support.exception.test;

import io.hhplus.concertreservation.api.data.repository.SeatRepository;
import io.hhplus.concertreservation.api.service.PaymentService;
import io.hhplus.concertreservation.api.service.QueueService;
import io.hhplus.concertreservation.api.support.exception.TokenValidationException;
/*
public class ExceptionTest {
    private final SeatRepository seatRepository;
    private final QueueService userTokenService;
    private final PaymentService userBalanceService;

    public ExceptionTest(SeatRepository seatRepository, QueueService userTokenService, PaymentService userBalanceService) {
        this.seatRepository = seatRepository;
        this.userTokenService = userTokenService;
        this.userBalanceService = userBalanceService;
    }

    public boolean reserveSeat(String userToken, String date, int seatNumber) {
        // 토큰 검증
        if (!userTokenService.isUserEligibleToken(userToken)) {
            throw new TokenValidationException();
        }

        // 좌석 상태 확인
        if (seatRepository.isSeatReserved(date, seatNumber)) {
            throw new SeatAlreadyReservedException();
        }

        // 잔액 확인
        if (!userBalanceService.hasSufficientBalance(userToken)) {
            throw new InsufficientBalanceException();
        }

        // 예약 처리
        boolean reserved = seatRepository.reserveSeat(userToken, date, seatNumber);
        if (!reserved) {
            throw new ConcertReservationException("Failed to reserve seat. Please try again.", 500);
        }

        return true;
    }
}
*/