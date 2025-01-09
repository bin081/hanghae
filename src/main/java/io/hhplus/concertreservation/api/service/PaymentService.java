package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.entity.Payment;
import io.hhplus.concertreservation.api.data.repository.PaymentRepository;
import io.hhplus.concertreservation.api.presentation.dto.PaymentRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final QueueService queueService;
    private final BalanceService balanceService;
    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;

    public PaymentService(QueueService queueService, BalanceService balanceService,
                          ReservationService reservationService, PaymentRepository paymentRepository) {
        this.queueService = queueService;
        this.balanceService = balanceService;
        this.reservationService = reservationService;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void processPayment(PaymentRequest request) {
        // 1. 대기열 검증
        String token = request.getToken();
        if (!queueService.isAllowedToReserve(token)) {
            throw new IllegalStateException("대기열 순서가 아님");
        }

        // 2. 좌석 임시 예약 상태 검증
        if (!reservationService.isSeatHeld(request.getDate(), request.getSeatNumber())) {
            throw new IllegalStateException("좌석 예약 시간이 만료되었거나 예약 불가");
        }

        // 3. 잔액 검증 및 결제 처리
        UUID userId = request.getUserId();
        double price = request.getAmount();
        if (!balanceService.hasSufficientBalance(userId, price)) {
            throw new IllegalStateException("잔액 부족");
        }
        balanceService.deductBalance(userId, price);

        // 4. 결제 기록 생성
        Payment payment = new Payment(userId, request.getDate(), request.getSeatNumber(), price);
        paymentRepository.save(payment);

        // 5. 좌석 소유권 확정 및 대기열 토큰 만료
        reservationService.confirmReservation(request.getDate(), request.getSeatNumber(), userId);
        queueService.leaveQueue(token);
    }


}
