package io.hhplus.concertreservation.api.data.repository2;

import io.hhplus.concertreservation.api.data.entity2.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
}