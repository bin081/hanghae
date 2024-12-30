package io.hhplus.architecture.lecture.domain.registration;
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
public class RegistrationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int registrationId;

    @Column(name="lectureId")
    private int lectureId;

    @Column(name="userId")
    private int userId;

    @Column(name="registration_time")
    private LocalDate  registration_time;

    @Column(name="status")
    private String status;

}
