package io.hhplus.concertreservation.api.data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_queue")
public class UserQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_id")
    private Long id;

    @Column(name = "token", nullable = false, length = 1000)
    private String token;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "entered_at", nullable = false)
    private LocalDateTime enteredAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "position", length = 100)
    private int position;
    // Getters and Setters
}
