package io.hhplus.concertreservation.api.data.repository;

import io.hhplus.concertreservation.api.data.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
