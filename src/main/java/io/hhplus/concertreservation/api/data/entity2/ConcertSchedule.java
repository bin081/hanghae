package io.hhplus.concertreservation.api.data.entity2;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "concert_schedual")
public class ConcertSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "concert_start_at", nullable = false)
    private LocalDateTime concertStartAt;

    @Column(name = "concert_end_at", nullable = false)
    private LocalDateTime concertEndAt;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    // Getters and Setters
}