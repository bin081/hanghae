package io.hhplus.concertreservation.redis;

import io.hhplus.concertreservation.api.service.QueueService_redis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QueueServiceTest {

    private static final String QUEUE_KEY = "concert_queue";

    @Autowired
    private QueueService_redis queueService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    public void setup() {
        // 테스트 시작 전에 기존 대기열 데이터를 삭제하여 깨끗한 상태로 만듭니다.
        redisTemplate.delete(QUEUE_KEY);
    }

    @Test
    public void testJoinQueueAndCheckPosition() {
        // 임의의 userId로 대기열에 추가
        UUID userId = UUID.randomUUID();
        String token = queueService.joinQueue(userId);

        // 대기열에 등록된 토큰의 순위는 1이어야 합니다.
        int position = queueService.checkQueuePosition(token);
        assertEquals(1, position, "대기열의 첫 번째 순위여야 합니다.");
    }

    @Test
    public void testIsAllowedToReserve() {
        // 대기열에 한 명 등록한 경우, 1순위이므로 예약 가능해야 합니다.
        UUID userId = UUID.randomUUID();
        String token = queueService.joinQueue(userId);
        boolean allowed = queueService.isAllowedToReserve(token);
        assertTrue(allowed, "대기열의 1순위는 예약 가능해야 합니다.");
    }

    @Test
    public void testLeaveQueue() {
        // 대기열에 등록 후, 탈퇴시키면 다시 순위를 조회했을 때 -1이어야 합니다.
        UUID userId = UUID.randomUUID();
        String token = queueService.joinQueue(userId);
        queueService.leaveQueue(token);
        int position = queueService.checkQueuePosition(token);
        assertEquals(-1, position, "탈퇴한 토큰은 대기열에서 조회시 -1이어야 합니다.");
    }

    @Test
    public void testActivateToken() {
        // activateToken() 호출 시 대기열에서 제거되므로 순위가 -1이어야 합니다.
        UUID userId = UUID.randomUUID();
        String token = queueService.joinQueue(userId);
        queueService.activateToken(token);
        int position = queueService.checkQueuePosition(token);
        assertEquals(-1, position, "activateToken 호출 후에는 해당 토큰이 대기열에 없어야 합니다.");
    }
}
