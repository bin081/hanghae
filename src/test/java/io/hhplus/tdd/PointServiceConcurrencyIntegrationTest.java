package io.hhplus.tdd;

import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)  // 웹 환경 설정
@AutoConfigureWebTestClient
public class PointServiceConcurrencyIntegrationTest {

    @Autowired
    private PointService pointService;  // 실제 서비스

    @Autowired
    private WebTestClient webTestClient;  // WebTestClient 자동 주입

    private static final long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 초기화
        // 예시로 사용자 포인트 10,000원 충전
        pointService.chargePoint(USER_ID, 20000);
    }

    @DisplayName("동시에 여러 스레드에서 포인트 충전을 시도할 때, 과다 충전되지 않도록 한다.")
    @Test
    void givenConcurrentChargeRequests_whenChargingPoints_thenShouldNotOvercharge() throws Exception {

        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // ExecutorService를 사용해 여러 스레드에서 동시 요청을 보낸다.
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    // 포인트 충전 요청
                    pointService.chargePoint(USER_ID, 5000);  // 충전 금액 5,000원
                } catch (Exception e) {
                    // 예외 발생시 출력 (락이 제대로 동작하지 않으면 예외가 발생할 수 있다.)
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();  // 모든 스레드가 작업을 끝낼 때까지 대기

        // 충전된 포인트가 최대 50,000원(10,000 + 5,000*10)보다 크지 않도록 해야 한다.
        UserPoint userPoint = pointService.getUserPoint(USER_ID);
        assertThat(userPoint.point()).isLessThanOrEqualTo(50000);
    }

    @DisplayName("동시에 여러 스레드에서 포인트 사용을 시도할 때, 잔액이 부족하면 사용되지 않도록 한다.")
    @Test
    void givenConcurrentUseRequests_whenUsingPoints_thenShouldNotAllowOveruse() throws Exception {

        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // ExecutorService를 사용해 여러 스레드에서 동시 요청을 보낸다.
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    // 포인트 사용 요청 (각각 2,000원씩 사용 시도)
                    pointService.usePoint(USER_ID, 2000);
                } catch (Exception e) {
                    // 예외 발생시 출력 (잔액 부족 시 예외가 발생할 수 있다.)
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();  // 모든 스레드가 작업을 끝낼 때까지 대기

        // 사용 후 잔액이 20,000원 - 2,000 * 10보다 크지 않도록 해야 한다.
        UserPoint userPoint = pointService.getUserPoint(USER_ID);
        assertThat(userPoint.point()).isGreaterThanOrEqualTo(0);
    }

    // 추가적으로 각종 동시성 관련 테스트를 구현할 수 있음
}
