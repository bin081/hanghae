package io.hhplus.concertreservation.api.data.repository2;

import io.hhplus.concertreservation.api.data.entity2.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByConcertScheduleIdAndStatus(String concertScheduleId, String status);
    // 특정 concertScheduleId에 해당하는 좌석을 조회하고, 상태가 "AVAILABLE"인 것만 반환
   /* List<Seat> findByConcertScheduleIdAndStatus(Long concertScheduleId, String status);*/
}