package io.hhplus.concert.mockapi.model;

public class WaitListEntry {
    private Long userId;         // 유저의 고유 ID
    private Long waitingTime;    // 대기 시간 (초 단위)

    public WaitListEntry(Long userId, Long waitingTime) {
        this.userId = userId;
        this.waitingTime = waitingTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Long waitingTime) {
        this.waitingTime = waitingTime;
    }
}
