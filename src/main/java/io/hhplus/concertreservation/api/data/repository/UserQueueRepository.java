package io.hhplus.concertreservation.api.data.repository;

import io.hhplus.concertreservation.api.data.entity.UserQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserQueueRepository extends JpaRepository<UserQueue, Long> {
    Optional<UserQueue> findByToken(String token);
}