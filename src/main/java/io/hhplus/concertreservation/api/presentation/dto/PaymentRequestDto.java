package io.hhplus.concertreservation.api.presentation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {

    private int user_id;
    private int seat_id;
    private double seat_amount;
    private LocalDateTime create_at;

}
