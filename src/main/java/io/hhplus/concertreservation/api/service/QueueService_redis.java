package io.hhplus.concertreservation.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class QueueService_redis {

    private static final String QUEUE_KEY = "concert_queue";
    private static final long QUEUE_EXPIRE_SECONDS = 300; // 5분 후 만료

    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOperations;

    @Autowired
    public QueueService_redis(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }  

    /**
     * 대기열에 유저 추가 (Redis Sorted Set 사용)
     */
    public String joinQueue(UUID userId) {
        String token = UUID.randomUUID().toString();
        double score = System.currentTimeMillis(); // 현재 시간을 점수로 사용
        zSetOperations.add(QUEUE_KEY, token, score);

        // Redis에서 만료 시간 설정 (5분 후 자동 삭제)
        redisTemplate.expire(QUEUE_KEY, QUEUE_EXPIRE_SECONDS, TimeUnit.SECONDS);

        return token;
    }

    /**
     * 대기열 내 현재 사용자 순위 확인
     */
    public int checkQueuePosition(String token) {
        Long rank = zSetOperations.rank(QUEUE_KEY, token);
        return (rank == null) ? -1 : rank.intValue() + 1; // Redis Rank는 0-based index이므로 +1
    }

    /**
     * 대기열에서 사용자 제거
     */
    public void leaveQueue(String token) {
        zSetOperations.remove(QUEUE_KEY, token);
    }

    /**
     * 예약이 가능한 사용자 확인 (대기열 1순위 여부 체크)
     */
    public boolean isAllowedToReserve(String token) {
        Set<String> topUsers = zSetOperations.range(QUEUE_KEY, 0, 0); // 대기열 첫 번째 사용자 조회
        return topUsers != null && topUsers.contains(token);
    }

    /**
     * 예약이 진행되면 토큰을 활성화 (대기열에서 제거)
     */
    public void activateToken(String token) {
        leaveQueue(token); // 예약이 진행되면 대기열에서 제거
    }

}
