package io.hhplus.concertreservation.api.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatResponse {
    private Long seatNum;
    private Double price;
    private String status;

    public SeatResponse(Long seatNumber, Double price, String status) {
    }

    public Long getSeatNum() {
        return seatNum;
    }
}