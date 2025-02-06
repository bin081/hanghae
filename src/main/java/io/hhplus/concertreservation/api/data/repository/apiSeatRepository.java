package io.hhplus.concertreservation.api.data.repository;


import io.hhplus.concertreservation.api.data.entity.SeatEntityApi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface apiSeatRepository extends JpaRepository<SeatEntityApi, Long> {

    List<SeatEntityApi> findByConcertScheduleIdAndStatus(String concertScheduleId, String status);

    Optional<SeatEntityApi> findBySeatNumber(Long seatNumber);

}