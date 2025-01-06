package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SeatReservationService {

    /*좌석 예약 요청하는 서비스(스케줄러 호출)*/

    private final ReservationRepository reservationRepository;
    private final QueueService queueService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public SeatReservationService(ReservationRepository reservationRepository, QueueService queueService) {
        this.reservationRepository = reservationRepository;
        this.queueService = queueService;
    }


    // 좌석 예약 요청 메서드
    public boolean reserveSeat(String userToken, String date, int seatNumber) {
        if (queueService.isUserEligible(userToken)) {
            String key = date + "-" + seatNumber;
            if (reservationRepository.isSeatReserved(key)) {
                return false; // 좌석이 이미 예약된 경우
            }

            // 좌석을 5분 동안 임시 배정
            scheduler.schedule(() -> reservationRepository.removeReservedSeat(key), 5, TimeUnit.MINUTES);
            reservationRepository.reserveSeat(key, userToken); // 임시 배정
            return true;
        }
        return false; // 대기열 검증 실패
    }
}
