package io.hhplus.concertreservation.api.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
}
