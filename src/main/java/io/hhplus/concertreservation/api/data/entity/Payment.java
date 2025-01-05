package io.hhplus.concertreservation.api.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    private int user_id;
    private int seat_id;
    private double amount;
    private LocalDateTime create_at;

}
