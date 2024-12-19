package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointService {

    // 동시성 제어 - ReentrantLock 인스턴스 생성
    private final Lock lock = new ReentrantLock();
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    // 1. 특정 유저의 포인트 조회
    public UserPoint getUserPoint(long userId) throws Exception {

       UserPoint point =  userPointTable.selectById(userId);
        if (point != null){
            return userPointTable.selectById(userId);
        }else{
            throw new Exception("포인트 정보가 없습니다.");
        }
    }

    // 2. 특정 유저의 포인트 사용 내역 조회
    public List<PointHistory> getPointHistory(long userId) throws Exception {
        List<PointHistory>  history = pointHistoryTable.selectAllByUserId(userId);

        if (history != null){
            return pointHistoryTable.selectAllByUserId(userId);
        }else{
            throw new Exception("포인트 사용내역 정보가 없습니다.");
        }
    }

    // 3. 포인트 충전
    public UserPoint chargePoint(long userId, long amount) {

        lock.lock();  // lock 획득

        try {
            // 금액이 500,000원을 초과하면 예외 발생
            if (amount < 0 || amount > 500000) {
                throw new IllegalArgumentException("충전 금액은 0원에서 500,000원 사이여야 합니다.");
            }

            // 현재 포인트 조회
            UserPoint currentPoint = userPointTable.selectById(userId);
            long newBalance = currentPoint.point() + amount;

            // 포인트 업데이트
            userPointTable.insertOrUpdate(userId, newBalance);

            // 포인트 충전 내역 기록
            pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());

            return userPointTable.selectById(userId);
        } finally {
            lock.unlock();  // 작업 후 unlock
        }
    }

    // 4. 포인트 사용
    public UserPoint usePoint(long userId, long amount) {

        lock.lock();  // lock 획득
        try {
            // 금액이 100원 미만이면 예외 발생
            if (amount < 100) {
                throw new IllegalArgumentException("사용 금액은 최소 100원이어야 합니다.");
            }

            // 금액이 100,000원을 초과하면 예외 발생
            if (amount > 100000) {
                throw new IllegalArgumentException("사용 금액은 최대 100,000원까지 가능합니다.");
            }

            // 현재 포인트 조회
            UserPoint currentPoint = userPointTable.selectById(userId);
            if (currentPoint.point() < amount) {
                throw new IllegalArgumentException("포인트가 부족합니다.");
            }

            // 포인트 차감 및 업데이트
            long newBalance = currentPoint.point() - amount;
            userPointTable.insertOrUpdate(userId, newBalance);

            // 포인트 사용 내역 기록
            pointHistoryTable.insert(userId, -amount, TransactionType.USE, System.currentTimeMillis());

            return userPointTable.selectById(userId);
        } finally {
            lock.unlock();  // 작업 후 unlock
        }
    }
}
