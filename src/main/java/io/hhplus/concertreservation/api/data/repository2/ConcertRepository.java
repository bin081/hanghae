package io.hhplus.concertreservation.api.data.repository2;

import io.hhplus.concertreservation.api.data.entity2.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    List<Concert> findByReservationStartAtBeforeAndReservationEndAtAfter(LocalDateTime start, LocalDateTime end);
}
