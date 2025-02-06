package io.hhplus.concertreservation.api.presentation.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BalanceResponse {
    private Long userId;
    private Integer balance; // 잔액 (단위: cents)
    private String currency;

    public BalanceResponse(Long userId, Integer balance, String currency) {
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
    }

    public BalanceResponse() {

    }
}
