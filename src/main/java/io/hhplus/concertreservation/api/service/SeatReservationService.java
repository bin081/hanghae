package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.entity.UserQueue;

import io.hhplus.concertreservation.api.data.repository.ReservationRepository;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.*;

@Service
public class SeatReservationService {

    /*좌석 예약 요청하는 서비스(스케줄러 호출)*/

    private final ReservationRepository reservationRepository;
    private final QueueService queueService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Autowired
    private final UserQueueRepository userQueueRepository;

    private final ConcurrentMap<String, String> reservedSeats = new ConcurrentHashMap<>();

    @Autowired
    public SeatReservationService(ReservationRepository reservationRepository, QueueService queueService, UserQueueRepository userQueueRepository) {
        this.reservationRepository = reservationRepository;
        this.queueService = queueService;
        this.userQueueRepository = userQueueRepository;
    }

    // 좌석 예약 요청 메서드
    public boolean reserveSeat(String userToken, String date, int seatNumber) {
        Optional<UserQueue> userQueue = userQueueRepository.findByToken(userToken);

        // Optional.empty()인 경우 바로 false 반환
        if (userQueue.isEmpty()) {
            return false;
        }

        if (queueService.isUserEligible(userQueue)) {
            String key = date + "-" + seatNumber;
            if (this.isSeatReserved(key)) {
                return false; // 좌석이 이미 예약된 경우
            }

            // 좌석을 5분 동안 임시 배정
            scheduler.schedule(() -> this.removeReservedSeat(key), 5, TimeUnit.MINUTES);
            this.reserveTemporarySeat(key, userToken); // 임시 배정
            return true;
        }
        return false; // 대기열 검증 실패
    }


    // 좌석을 임시로 예약 상태로 저장하는 메서드
    public void reserveTemporarySeat(String key, String userToken) {
        reservedSeats.put(key, userToken);
    }
    // 좌석이 예약되어 있는지 확인
    public boolean isSeatReserved(String key) {
        return reservedSeats.containsKey(key);
    }



    // 좌석 예약 해제
    public void removeReservedSeat(String key) {
        reservedSeats.remove(key);
    }
}
