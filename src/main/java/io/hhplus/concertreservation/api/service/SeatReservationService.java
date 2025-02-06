package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.entity.Reservation;

import io.hhplus.concertreservation.api.data.entity.SeatEntityApi;
import io.hhplus.concertreservation.api.data.repository.ReservationRepository;
import io.hhplus.concertreservation.api.data.repository.apiSeatRepository;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.*;

@Service
@Transactional
public class SeatReservationService {

    private final ReservationRepository reservationRepository;
    private final QueueService queueService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Autowired
    private final UserQueueRepository userQueueRepository;
    @Autowired
    private apiSeatRepository apiSeatRepository;
    private final ConcurrentMap<String, String> reservedSeats = new ConcurrentHashMap<>();

    @Autowired
    public SeatReservationService(ReservationRepository reservationRepository, QueueService queueService, UserQueueRepository userQueueRepository) {
        this.reservationRepository = reservationRepository;
        this.queueService = queueService;
        this.userQueueRepository = userQueueRepository;
    }



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public boolean reserveSeat(String userToken, LocalDate date, long seatNumber) {

        String key = date + "-" + seatNumber;
        System.out.println("Requested seatEntityApi number: " + seatNumber);

        SeatEntityApi seatEntityApi = apiSeatRepository.findBySeatNumber(seatNumber).orElseThrow(() -> new IllegalArgumentException("SeatEntityConcurrency not found"));

        // "2025-01-16" 날짜 문자열을 LocalDateTime으로 변환
        String formattedDate = date.toString(); // "2025-01-16"
        System.out.println("Searching for reservation on date: " + formattedDate + " for seatEntityApi: " + seatNumber);

        // DB에서 예약 상태 확인 및 갱신
        Optional<Reservation> reservation1 = reservationRepository.findByDateAndSeat(formattedDate, seatEntityApi);

        if (reservation1.isPresent()) {
            if (reservation1.get().isReserved()) {
                System.out.println("Reservation failed: SeatEntityConcurrency already reserved.");
                return false;
            } else {
                // 좌석 예약 상태 갱신
                reservation1.get().setReserved(true);
                reservationRepository.save(reservation1.get());
                System.out.println("SeatEntityConcurrency reserved successfully for user: " + userToken);
            }
        } else {
            Optional<SeatEntityApi> seatOptional = apiSeatRepository.findById((long) seatNumber);
            SeatEntityApi seatEntityApi2 = seatOptional.get();
            // 좌석이 존재하지 않으면 새로운 예약 생성
            Reservation newReservation = new Reservation();
            newReservation.setDate(String.valueOf(date));
            newReservation.setSeatEntityApi(seatEntityApi2);
            newReservation.setReserved(true);
            reservationRepository.save(newReservation);
            System.out.println("SeatEntityConcurrency reserved successfully for user: " + userToken);
        }

        // 예약 만료 스케줄 설정 (5분 후 해제)
        scheduler.schedule(() -> releaseSeat(date, (int) seatNumber), 5, TimeUnit.MINUTES);
        return true;
    }

    @Transactional
    public void releaseSeat(LocalDate date, long seatNumber) {
        SeatEntityApi seatEntityApi = apiSeatRepository.findById(seatNumber).orElseThrow(() -> new IllegalArgumentException("SeatEntityConcurrency not found"));
        Optional<Reservation> reservation = reservationRepository.findByDateAndSeat(String.valueOf(date), seatEntityApi);
        if (reservation.isPresent() && reservation.get().isReserved()) {
            reservation.get().setReserved(false);
            reservationRepository.save(reservation.get());
            System.out.println("SeatEntityConcurrency released for date: " + date + ", seatEntityApi number: " + seatNumber);
        }
    }

   /*
    // 좌석 예약 요청 메서드
    public boolean reserveSeat(String userToken, String date, int seatNumber) {
        // 비관적 락을 사용하여 DB에서 사용자 정보를 가져옴
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
    }*/

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
        if (reservedSeats.remove(key) != null) {
            System.out.println("SeatEntityConcurrency " + key + " has been released.");
        } else {
            System.out.println("SeatEntityConcurrency " + key + " was not reserved.");
        }
    }
}
