package io.hhplus.concertreservation.concurrency;

import io.hhplus.concertreservation.concurrency.Entity.SeatEntityConcurrency;
import io.hhplus.concertreservation.concurrency.Service.SeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeatEntityApiEntityConcurrencyServiceTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private io.hhplus.concertreservation.concurrency.Repository.concurrencySeatRepository concurrencySeatRepository;

    @Test
    @Transactional
    void testSeatReservation() {
        // 초기 데이터 설정
        SeatEntityConcurrency seatEntityConcurrency = new SeatEntityConcurrency();
        seatEntityConcurrency.setSeatNumber(1L);
        seatEntityConcurrency.setStatus(SeatEntityConcurrency.SeatStatus.AVAILABLE);
        concurrencySeatRepository.save(seatEntityConcurrency);

        String userId = "user1";

        // 1. 좌석 임시 예약
        seatService.reserveSeat(1L, userId);
        SeatEntityConcurrency heldSeatEntityConcurrency = concurrencySeatRepository.findBySeatNumberWithLock(1L).orElseThrow();

        assertEquals(SeatEntityConcurrency.SeatStatus.TEMPORARY_HOLD, heldSeatEntityConcurrency.getStatus());
        assertEquals(userId, heldSeatEntityConcurrency.getReservedBy());

        // 2. 최종 예약
        seatService.reserveSeat(1L, userId);
        SeatEntityConcurrency reservedSeatEntityConcurrency = concurrencySeatRepository.findBySeatNumberWithLock(1L).orElseThrow();

        assertEquals(SeatEntityConcurrency.SeatStatus.RESERVED, reservedSeatEntityConcurrency.getStatus());
        assertEquals(userId, reservedSeatEntityConcurrency.getReservedBy());
    }
}
