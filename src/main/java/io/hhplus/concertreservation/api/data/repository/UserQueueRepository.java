package io.hhplus.concertreservation.api.data.repository;

import io.hhplus.concertreservation.api.data.entity.UserQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface UserQueueRepository extends JpaRepository<UserQueue, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserQueue> findByToken(String token);
}