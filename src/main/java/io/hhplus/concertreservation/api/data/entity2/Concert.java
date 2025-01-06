package io.hhplus.concertreservation.api.data.entity2;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "concert")
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "singer", nullable = false, length = 100)
    private String singer;

    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Column(name = "reservation_start_at", nullable = false)
    private LocalDateTime reservationStartAt;

    @Column(name = "reservation_end_at", nullable = false)
    private LocalDateTime reservationEndAt;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    // Getters and Setters
}