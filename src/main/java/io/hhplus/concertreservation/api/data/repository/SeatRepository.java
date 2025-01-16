package io.hhplus.concertreservation.api.data.repository;


import io.hhplus.concertreservation.api.data.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByConcertScheduleIdAndStatus(String concertScheduleId, String status);

    Optional<Seat> findBySeatNumber(Long seatNumber);

}