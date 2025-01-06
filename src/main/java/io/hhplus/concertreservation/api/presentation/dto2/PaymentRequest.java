package io.hhplus.concertreservation.api.presentation.dto2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long userId;
    private Double amount;
}