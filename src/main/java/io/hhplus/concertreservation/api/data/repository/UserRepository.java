package io.hhplus.concertreservation.api.data.repository;

import io.hhplus.concertreservation.api.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
