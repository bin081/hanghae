package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.presentation.dto.UserTokenReponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TokenService {

    /*대기열 토큰을 생성하고 유효성 검사를 수행하는 서비스*/

    private final ConcurrentHashMap<String, String> tokenStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> waitList = new ConcurrentHashMap<>();

    public UserTokenReponseDto generateToken(String uuid){
        String token = UUID.randomUUID().toString();
        int positionInQueue = waitList.size()+1;
        tokenStorage.put(token, uuid);
        waitList.put(uuid, positionInQueue);
        log.info("token : ", token , ",  positionInQueue : " , positionInQueue);
        return new UserTokenReponseDto(token, positionInQueue);
    }

    public boolean validateToken(String token){
        String uuid = tokenStorage.get(token);
        log.info("uuid : " , uuid);
        if (uuid == null){
            return false; //토큰이 없으면 유효하지 않음
        }
        // 대기열에서 해당 uuid의 상태를 확인하는 로직 추가될 것
        return true;
    }
}
