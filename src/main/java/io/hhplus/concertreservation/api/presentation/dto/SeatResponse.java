package io.hhplus.concertreservation.api.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatResponse {
    private Long seatNum;
    private Double price;
    private String status;


    public Long getSeatNum() {
        return seatNum;
    }

    public SeatResponse(Long seatNum, Double price, String status) {
        this.seatNum = seatNum;
        this.price = price;
        this.status = status;
    }
}