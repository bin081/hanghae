package io.hhplus.concertreservation.api.data.repository;

import io.hhplus.concertreservation.api.data.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatReservationRepository extends JpaRepository<Reservation, String> {

    // 특정 날짜에 예약되지 않은 좌석을 조회
    List<Reservation> findByDateAndIsReservedFalse(String date);

    // 특정 날짜에 예약된 좌석을 조회
    List<Reservation> findByDateAndIsReservedTrue(String date);
}