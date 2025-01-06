package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.entity.Reservation;
import io.hhplus.concertreservation.api.data.repository.SeatReservationRepository;
import io.hhplus.concertreservation.api.presentation.dto.SeatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationService {

   /* 예약가능한 날짜와 좌석을 조회하는 서비스*/

    private final SeatReservationRepository reservationRepository;
    private final ConcurrentHashMap<String, List<Integer>> availableSeats = new ConcurrentHashMap<>();
    private final List<String> availableDates = new ArrayList<>();
    private final ConcurrentMap<String, ScheduledFuture<?>> reservedSeats = new ConcurrentHashMap<>();

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    public ReservationService(SeatReservationRepository reservationRepository, ThreadPoolTaskScheduler taskScheduler) {
        this.reservationRepository = reservationRepository;
        this.taskScheduler = taskScheduler;
    }


    /* 예약 가능 날짜 조회 */
    public List<String> getAvailableDates() {
        List<String> availableDates = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation reservation : reservations) {
            if (!reservation.isReserved()) {
                availableDates.add(reservation.getDate());
            }
        }
        return Collections.unmodifiableList(availableDates);
    }

    /* 예약 가능 좌석 조회 */
    public List<SeatResponse> getAvailableSeats(String date) {
        List<SeatResponse> response = new ArrayList<>();
        // 특정 날짜에 예약되지 않은 좌석을 DB에서 조회
        List<Reservation> availableReservations = reservationRepository.findByDateAndIsReservedFalse(date);

        for (Reservation reservation : availableReservations) {
            response.add(new SeatResponse(reservation.getSeatNumber()));
        }

        return response;
    }

    /* 좌석 예약 요청 */
    public boolean reserveSeat(String date, int seatNumber) {
        String key = date + "-" + seatNumber;

        // 해당 좌석 예약 상태 확인
        Reservation reservation = reservationRepository.findById(key).orElse(null);
        if (reservation == null || reservation.isReserved()) {
            return false; // 좌석이 없거나 이미 예약된 경우
        }

        // 임시 배정 처리 (5분 후 만료)
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            reservation.setReserved(false);  // 예약 해제
            reservationRepository.save(reservation);  // DB에 변경사항 저장
        }, new java.util.Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)));

        // 임시 예약 상태로 변경
        reservation.setReserved(true);
        reservation.setReservedUntil(new java.util.Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)));
        reservationRepository.save(reservation); // DB에 저장

        return true;
    }
}
