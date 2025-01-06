package io.hhplus.concertreservation.api.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class QueueService {

    /*예약요청이 들어오면, 대기열에 따라 순차적으로 좌석을 배정하는 서비스(동시성 고려)*/
    private final ConcurrentHashMap<String, List<Integer>> availableSeats = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public String reserveSeat(String uuid, String date){
        lock.lock();
        try {
            List<Integer> seats = availableSeats.get(date);
            if (seats != null && !seats.isEmpty()){
                Integer seat = seats.remove(0); // 첫 번째 좌석을 예약
                return "예약완료 : 좌석 " + seat + "예약됨";
            }
            return "에약 실패 : 좌석이 없습니다.";
        }finally {
            lock.unlock();
        }
    }

    private final ConcurrentMap<String, Integer> userQueue = new ConcurrentHashMap<>();

    // 유저가 대기열에 있는지, 그리고 대기열 검증
    public boolean isUserEligible(String userToken) {
        Integer queuePosition = userQueue.get(userToken);
        return queuePosition != null && queuePosition <= 10; // 예시: 대기열에서 앞 10명만 예약 가능
    }

    // 대기열에 유저 추가
    public void addUserToQueue(String userToken) {
        userQueue.put(userToken, userQueue.size() + 1);
    }
}
