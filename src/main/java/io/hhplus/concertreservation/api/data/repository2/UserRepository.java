package io.hhplus.concertreservation.api.data.repository2;

import io.hhplus.concertreservation.api.data.entity2.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
