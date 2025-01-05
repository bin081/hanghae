package io.hhplus.concertreservation.api.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConcertSchedule {

    private int concert_id;
    private LocalDateTime open_date;
    private LocalDateTime start_at;
    private LocalDateTime end_at;
    private int total_seat;
    private String status;

}
