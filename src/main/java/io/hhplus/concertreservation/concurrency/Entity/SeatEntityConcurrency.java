package io.hhplus.concertreservation.concurrency.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity(name = "SeatEntityConcurrency")
public class SeatEntityConcurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="seatNumber")
    private Long seatNumber;
    @Column(name="reservedBy")
    private String reservedBy; // 예약한 사용자 UUID
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private SeatStatus status; // AVAILABLE, TEMPORARY_HOLD, RESERVED
    @Column(name="version")
    @Version // 낙관적 락을 위한 버전 필드
    private Long version;

    public enum SeatStatus {
        AVAILABLE,
        TEMPORARY_HOLD,
        RESERVED
    }

    public void reserve(String userId) {
        if (this.status == SeatStatus.AVAILABLE || this.status == SeatStatus.TEMPORARY_HOLD) {
            this.status = SeatStatus.RESERVED;
            this.reservedBy = userId;
        } else {
            throw new IllegalStateException("This seat is not available for reservation.");
        }
    }

    public void hold(String userId) {
        if (this.status == SeatStatus.AVAILABLE) {
            this.status = SeatStatus.TEMPORARY_HOLD;
            this.reservedBy = userId;
        } else {
            throw new IllegalStateException("This seat is already reserved or on hold.");
        }
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.reservedBy = null;
    }
}
