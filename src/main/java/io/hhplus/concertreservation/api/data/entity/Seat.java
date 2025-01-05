package io.hhplus.concertreservation.api.data.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

    private int concert_id;
    private double amount;
    private int seat_id;
    private LocalDateTime created_at;

}
