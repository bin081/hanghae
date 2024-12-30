package io.hhplus.architecture.lecture.domain.lecture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LectureInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lectureId;

    @Column(name="currentParticipants")
    private int currentParticipants;

    @Column(name="start_time")
    private LocalDate start_time;

    @Column(name="end_time")
    private LocalDate end_time;

    @Column(name="maxParticipants")
    private int maxParticipants;

    @Column(name="speaker")
    private String speaker;

    @Column(name="date")
    private LocalDate date;

    @Column(name="title")
    private String title;

}
