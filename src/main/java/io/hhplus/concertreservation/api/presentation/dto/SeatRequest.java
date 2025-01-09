package io.hhplus.concertreservation.api.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatRequest {
    private Long userId;
    private Long concertId;
    private Long seatId;
    private Double amount;
}
