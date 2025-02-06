package io.hhplus.concertreservation.concurrency.Repository;

import io.hhplus.concertreservation.api.data.entity.Reservation;
import io.hhplus.concertreservation.concurrency.Entity.SeatEntityConcurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository("concurrencySeatRepository")
public interface concurrencySeatRepository extends JpaRepository<SeatEntityConcurrency, Long> {

    /*@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntityConcurrency s WHERE s.seatNumber = :seatNumber")
    Optional<SeatEntityConcurrency> findBySeatNumberWithLock(Integer seatNumber);*/

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM SeatEntityConcurrency r WHERE r.seatNumber = :seatNumber")
    Optional<SeatEntityConcurrency> findBySeatNumberWithLock(@Param("seatNumber") Long seatNumber);

}


