package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.entity.UserQueue;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class QueueService {

    /*예약요청이 들어오면, 대기열에 따라 순차적으로 좌석을 배정하는 서비스(동시성 고려)*/
    private final ConcurrentHashMap<String, List<Integer>> availableSeats = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final ConcurrentMap<String, Long> userQueueMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> queuePosition =  new ConcurrentHashMap<>();
    @Autowired
    private final UserQueueRepository userQueueRepository;

    public QueueService(UserQueueRepository userQueueRepository) {
        this.userQueueRepository = userQueueRepository;
    }

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

    // 유저가 대기열에 있는지, 그리고 대기열 검증
    /*public boolean isUserEligible(Optional<UserQueue> userQueue) {
        int position = queuePosition.size() + 1;
        queuePosition.put(userQueue.get().getToken(), position);
        userQueueMap.put(userQueue.get().getToken(), userQueue.get().getId());
        if (userQueue.get().getExpiredAt().isBefore(LocalDateTime.now())){
            System.out.println("Queue has expired.");
        }else
            System.out.println("Queue is still valid.");
            return queuePosition != null && queuePosition.get(position) <= 10;
    }*/

    public boolean isUserEligible(Optional<UserQueue> userQueue) {
        if (userQueue.isEmpty() || userQueue.get().getExpiredAt().isBefore(LocalDateTime.now())) {
            return false; // 만료된 경우
        }

        String token = userQueue.get().getToken();
        int position = queuePosition.getOrDefault(token, Integer.MAX_VALUE); // 기본값은 매우 큰 숫자
        return position <= 10; // 대기열 상위 10명만 예약 가능
    }

    public boolean isUserEligibleToken(String token) {
        int position = queuePosition.size() + 1;
        queuePosition.put(token, position);
        Optional<UserQueue> userQueue = userQueueRepository.findByToken(token);
        if (userQueue.get().getExpiredAt().isBefore(LocalDateTime.now())){
            System.out.println("Queue has expired.");
        }else
            System.out.println("Queue is still valid.");
        return queuePosition != null && queuePosition.get(position) <= 10;
    }

    // 대기열에 유저 추가
    public UserQueue addUserToQueue(String userToken) {
        UserQueue userQueue = new UserQueue();
        userQueue.setToken(userToken);
        userQueue.setEnteredAt(LocalDateTime.now().plusMinutes(5));
        userQueue.setStatus("WAITING");
        userQueueRepository.save(userQueue);
        return userQueue;
    }

    private final Map<String, Integer> queue = new LinkedHashMap<>();
    private final Set<String> activeTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public synchronized String joinQueue(UUID userId) {
        String token = UUID.randomUUID().toString();
        queue.put(token, queue.size() + 1);
        return token;
    }

    public synchronized int checkQueuePosition(String token) {
        return queue.getOrDefault(token, -1);
    }

    public synchronized void leaveQueue(String token) {
        queue.remove(token);
        activeTokens.remove(token);
    }

    public synchronized boolean isAllowedToReserve(String token) {
        return !activeTokens.contains(token) && queue.entrySet().iterator().next().getKey().equals(token);
    }

    public synchronized void activateToken(String token) {

        activeTokens.add(token);
    }


}
