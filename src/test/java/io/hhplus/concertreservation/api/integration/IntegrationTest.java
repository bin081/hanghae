package io.hhplus.concertreservation.api.integration;

import io.hhplus.concertreservation.api.data.entity.UserQueue;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import io.hhplus.concertreservation.api.service.SeatReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class IntegrationTest {
    @Autowired
    private SeatReservationService seatReservationService;

    @Autowired
    private UserQueueRepository userQueueRepository;

    private static final String TEST_DATE = "2025-01-20";
    private static final int TEST_SEAT_NUMBER = 1;
    private static final String USER_TOKEN_1 = "user1-token";
    private static final String USER_TOKEN_2 = "user2-token";

    @BeforeEach
    public void setup() {
        // Mock or create user queues in the repository
        UserQueue user1 = new UserQueue();
        user1.setToken(USER_TOKEN_1);
        user1.setEnteredAt(LocalDateTime.now());
        user1.setExpiredAt(java.time.LocalDateTime.now().plusMinutes(10));
        userQueueRepository.save(user1);

        UserQueue user2 = new UserQueue();
        user2.setToken(USER_TOKEN_2);
        user2.setEnteredAt(LocalDateTime.now());
        user2.setExpiredAt(java.time.LocalDateTime.now().plusMinutes(10));
        userQueueRepository.save(user2);
    }

    @Test
    public void testConcurrentSeatReservation1() throws InterruptedException {
        // ThreadPoolExecutor
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // CountDownLatch: 두 스레드가 동시에 시작하도록 동기화
        CountDownLatch latch = new CountDownLatch(1);

        // 결과 저장을 위한 AtomicBoolean
        AtomicBoolean user1Success = new AtomicBoolean(false);
        AtomicBoolean user2Success = new AtomicBoolean(false);

        String dateString = "2025-01-16 22:55:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(dateString, formatter);

        // user1 작업
        Runnable user1Task = () -> {
            try {
                latch.await(); // 모든 스레드가 동시에 시작되도록 대기
                boolean result = seatReservationService.reserveSeat("USER1", LocalDate.from(date), 1);
                user1Success.set(result);
                System.out.println("User1 result: " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // user2 작업
        Runnable user2Task = () -> {
            try {
                latch.await(); // 모든 스레드가 동시에 시작되도록 대기
                boolean result = seatReservationService.reserveSeat("USER2", LocalDate.from(date), 1);
                user2Success.set(result);
                System.out.println("User2 result: " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // 사용자 1과 2를 실행
        executor.submit(user1Task);
        executor.submit(user2Task);

        // 모든 스레드 시작
        latch.countDown(); // 두 스레드 동시에 시작

        // Executor 종료 기다림
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // 두 스레드 중 하나만 성공해야 하므로 XOR 비교
        assertTrue(user1Success.get() ^ user2Success.get(), "Only one user should succeed in reserving the seat.");
    }


    @Test
    public void testConcurrentSeatReservation2() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        AtomicBoolean user1Success = new AtomicBoolean(false);
        AtomicBoolean user2Success = new AtomicBoolean(false);

        String dateString = "2025-01-16 22:55:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(dateString, formatter);

        Runnable user1Task = () -> {
            try {
                latch.await(); // 모든 스레드가 동시에 시작
                boolean result = seatReservationService.reserveSeat("USER1", LocalDate.from(date), 1);
                user1Success.set(result);
                System.out.println("User1 result: " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable user2Task = () -> {
            try {
                latch.await(); // 모든 스레드가 동시에 시작
                boolean result = seatReservationService.reserveSeat("USER2", LocalDate.from(date), 1);
                user2Success.set(result);
                System.out.println("User2 result: " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        executor.submit(user1Task);
        executor.submit(user2Task);

        latch.countDown(); // 스레드 시작
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // 결과 검증
        assertTrue(user1Success.get() ^ user2Success.get(), "Only one user should succeed in reserving the seat.");
    }
}