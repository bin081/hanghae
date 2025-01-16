package io.hhplus.concertreservation.api.data.repository;

import io.hhplus.concertreservation.api.data.entity.Reservation;
import io.hhplus.concertreservation.api.data.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
@Transactional
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatNumber = :seatNumber")
    Optional<Seat> findBySeatNumber(@Param("seatNumber") Long seatNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.date = :date AND r.seat = :seat")
    Optional<Reservation> findByDateAndSeat(String date, Seat seat);
}