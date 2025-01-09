package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.entity.UserQueue;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TokenService {

    /*대기열 토큰을 생성하고 유효성 검사를 수행하는 서비스*/

    private final ConcurrentHashMap<String, Long> tokenStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> waitList = new ConcurrentHashMap<>();

    @Autowired
    private UserQueueRepository userQueueRepository;

    public String generateToken(Long userId){

        UserQueue userQueue = new UserQueue();
        String token = UUID.randomUUID().toString();
        int positionInQueue = waitList.size()+1;
        tokenStorage.put(token, userId);
        waitList.put(userId, positionInQueue);
        log.info("token : ", token , ",  positionInQueue : " , positionInQueue);
        userQueue.setEnteredAt(LocalDateTime.now());
        userQueue.setExpiredAt(LocalDateTime.now().plusMinutes(5)); // Set expiration to 5 minutes
        userQueue.setStatus("WAITING");
        userQueueRepository.save(userQueue);
        return userQueue.getToken();
    }

    public boolean validateToken(String token){
        String uuid = String.valueOf(tokenStorage.get(token));
        log.info("uuid : " , uuid);
        if (uuid == null){
            return false; //토큰이 없으면 유효하지 않음
        }
        // 대기열에서 해당 uuid의 상태를 확인하는 로직 추가될 것
        return true;
    }
}
