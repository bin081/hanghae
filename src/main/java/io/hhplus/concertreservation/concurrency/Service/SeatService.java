package io.hhplus.concertreservation.concurrency.Service;

import io.hhplus.concertreservation.concurrency.Entity.SeatEntityConcurrency;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Service
public class SeatService {

    private final io.hhplus.concertreservation.concurrency.Repository.concurrencySeatRepository concurrencySeatRepository;
    private final ThreadPoolTaskScheduler taskScheduler;

    public SeatService(
         @Qualifier("concurrencySeatRepository") io.hhplus.concertreservation.concurrency.Repository.concurrencySeatRepository concurrencySeatRepository,
         ThreadPoolTaskScheduler taskScheduler) {

        this.concurrencySeatRepository = concurrencySeatRepository;
        this.taskScheduler = taskScheduler;
    }
    private final int HOLD_DURATION_SECONDS = 300; // 5분

    // 좌석 예약 요청
    @Transactional
    public void reserveSeat(Long seatNumber, String userId) {
        SeatEntityConcurrency seatEntityConcurrency = concurrencySeatRepository.findBySeatNumberWithLock(seatNumber)
                .orElseThrow(() -> new IllegalArgumentException("SeatEntityConcurrency not found"));

        if (seatEntityConcurrency.getStatus() == SeatEntityConcurrency.SeatStatus.AVAILABLE) {
            seatEntityConcurrency.hold(userId); // 임시 홀딩
            scheduleRelease(seatEntityConcurrency.getId());
        } else if (seatEntityConcurrency.getStatus() == SeatEntityConcurrency.SeatStatus.TEMPORARY_HOLD
                && userId.equals(seatEntityConcurrency.getReservedBy())) {
            seatEntityConcurrency.reserve(userId); // 최종 예약
        } else {
            throw new IllegalStateException("SeatEntityConcurrency is not available.");
        }
    }

    // 좌석 임시 예약 해제 스케줄링
    private void scheduleRelease(Long seatId) {
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
            releaseSeat(seatId);
        }, Instant.now().plusSeconds(HOLD_DURATION_SECONDS));
    }

    // 좌석 임시 예약 해제
    @Transactional
    public void releaseSeat(Long seatId) {
        SeatEntityConcurrency seatEntityConcurrency = concurrencySeatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("SeatEntityConcurrency not found"));

        if (seatEntityConcurrency.getStatus() == SeatEntityConcurrency.SeatStatus.TEMPORARY_HOLD) {
            seatEntityConcurrency.release();
        }
    }
}
