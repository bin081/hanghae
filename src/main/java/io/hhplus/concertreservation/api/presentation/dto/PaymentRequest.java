package io.hhplus.concertreservation.api.presentation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Data
public class PaymentRequest {
    private UUID userId;
    private String token; // 대기열 검증 토큰
    private String date;
    private int seatNumber;
    private double amount;
}

