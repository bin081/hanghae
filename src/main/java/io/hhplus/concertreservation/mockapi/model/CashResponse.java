package io.hhplus.concertreservation.mockapi.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class CashResponse {

    private Long userId;
    private Double amount;


    public CashResponse(Long userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
