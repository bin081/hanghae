package io.hhplus.tdd;

import io.hhplus.tdd.point.*;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PointServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private PointService pointService;
    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @DisplayName("1. 특정 유저의 포인트를 조회하는 기능")
    @Test
    void getPoint() throws Exception {
        userPointTable.insertOrUpdate(1, 1000); // 초기 포인트 1000
        UserPoint userPoint = pointService.getUserPoint(1);
        Assertions.assertThat(userPoint.point()).isEqualTo(1000);
    }

    @DisplayName("2. 특정 유저의 포인트 사용 내역을 조회하는 기능")
    @Test
    void getPointHistory() throws Exception {
        userPointTable.insertOrUpdate(1, 1000);
        pointService.chargePoint(1, 500);
        pointService.usePoint(1, 200);

        List<PointHistory> history = pointService.getPointHistory(1);
        Assertions.assertThat(history.size()).isEqualTo(2);
        Assertions.assertThat(history.get(0).type()).isEqualTo(TransactionType.CHARGE);
        Assertions.assertThat(history.get(1).type()).isEqualTo(TransactionType.USE);
    }

    @DisplayName("3. 포인트를 충전하는 기능")
    @Test
    void chargePoint() {
        userPointTable.insertOrUpdate(1, 1000);
        UserPoint userPoint = pointService.chargePoint(1, 2000);
        Assertions.assertThat(userPoint.point()).isEqualTo(3000);
    }

    @DisplayName("4. 포인트를 사용하는 기능")
    @Test
    void usePointSuccess() {
        userPointTable.insertOrUpdate(1, 3000);
        UserPoint userPoint = pointService.usePoint(1, 1000);
        Assertions.assertThat(userPoint.point()).isEqualTo(2000);
    }

    @DisplayName("5. 잔고가 부족할 경우, 포인트 사용은 실패해야 한다")
    @Test
    void usePointFail1() {
        userPointTable.insertOrUpdate(1, 500);
        Assertions.assertThatThrownBy(() -> pointService.usePoint(1, 1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트가 부족합니다.");
    }

    @DisplayName("6. 포인트 사용이 100원 미만이면 실패해야 한다")
    @Test
    void usePointFail2() {
        userPointTable.insertOrUpdate(1, 3000);
        Assertions.assertThatThrownBy(() -> pointService.usePoint(1, 50))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용 금액은 최소 100원이어야 합니다.");
    }

    @DisplayName("7. 포인트 사용이 100,000원 이상이면 실패해야 한다")
    @Test
    void usePointFail3() {
        userPointTable.insertOrUpdate(1, 300000);
        Assertions.assertThatThrownBy(() -> pointService.usePoint(1, 200000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용 금액은 최대 100,000원까지 가능합니다.");
    }

    @DisplayName("8. 최대 보유 한도를 초과하는 포인트 충전은 실패해야 한다")
    @Test
    void chargePointFail() {
        userPointTable.insertOrUpdate(1, 499_000);
        Assertions.assertThatThrownBy(() -> pointService.chargePoint(1, 100_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전 금액은 0원에서 500,000원 사이여야 합니다.");
    }
}
