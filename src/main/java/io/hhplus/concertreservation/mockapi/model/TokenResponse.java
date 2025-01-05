package io.hhplus.concertreservation.mockapi.model;

import lombok.Data;

@Data
public class TokenResponse {
    private Long userId;         // 유저의 고유 ID
    private String token;        // 유저의 대기열 토큰
    private Long remainingTime;  // 대기열에서 남은 시간 (초 단위 또는 분 단위 등)

    // 기본 생성자
    public TokenResponse() {}

    // 전체 필드를 받는 생성자
    public TokenResponse(Long userId, String token, Long remainingTime) {
        this.userId = userId;
        this.token = token;
        this.remainingTime = remainingTime;
    }

    // Getter 및 Setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Long remainingTime) {
        this.remainingTime = remainingTime;
    }
}
