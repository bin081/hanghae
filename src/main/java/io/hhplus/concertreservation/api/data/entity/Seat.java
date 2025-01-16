package io.hhplus.concertreservation.api.data.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "concert_seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedual_id", nullable = false)
    private ConcertSchedule concertSchedule;

    @Column(name = "seatNum", nullable = false)
    private Long seatNumber;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date", nullable = true)
    private LocalDateTime updateDate;

    @Column(name = "status", length = 100)
    private String status;

    public Long getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Long seatNumber) {
        this.seatNumber = seatNumber;
    }

    // Getters and Setters
}