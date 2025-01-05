package io.hhplus.concertreservation.mockapi.service;

import io.hhplus.concertreservation.mockapi.model.WaitListEntry;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MockManager {
    // 대기열을 관리하는 큐 (메모리 내)
    private Queue<WaitListEntry> waitList = new LinkedList<>();

    private Map<Long, Double> userBalances = new HashMap<>();

    // 대기열에 유저를 추가하는 메소드
    public void insertWaitList(Long userId) {
        // 유저 대기 순서 계산 (현재 대기열의 크기 + 1)
        long waitingTime = calculateWaitingTime();

        // 대기열 항목에 유저 ID와 대기 시간을 저장
        waitList.add(new WaitListEntry(userId, waitingTime));

        // 로그를 통해 대기열 상태 확인
        System.out.println("유저 " + userId + "가 대기열에 추가되었습니다. 대기 시간: " + waitingTime + "초.");
    }

    // 대기 시간을 계산하는 로직 (예시로 대기열 크기에 비례하도록 설정)
    private long calculateWaitingTime() {
        // 대기열에 있는 유저 수에 비례하여 대기 시간 설정
        return waitList.size() * 10L;  // 대기열에 있는 각 유저마다 10초씩 추가
    }

    // 대기열에 있는 유저를 확인하는 메소드 (대기열 확인용)
    public Queue<WaitListEntry> getWaitList() {
        return waitList;
    }


    // 잔액 충전
    public void chargeCash(Long userId, Double amount) {
        userBalances.put(userId, userBalances.getOrDefault(userId, 0.0) + amount);
    }

    // 잔액 조회
    public Double getBalance(Long userId) {
        return userBalances.getOrDefault(userId, 0.0);
    }

}
