package io.hhplus.concertreservation.api.data.repository;

import io.hhplus.concertreservation.api.data.entity.Reservation;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public class ReservationRepository {
    private final ConcurrentMap<String, String> reservedSeats = new ConcurrentHashMap<>();

    // 좌석이 예약되어 있는지 확인
    public boolean isSeatReserved(String key) {
        return reservedSeats.containsKey(key);
    }

    // 좌석을 예약 상태로 저장
    public void reserveSeat(String key, String userToken) {
        reservedSeats.put(key, userToken);
    }

    // 좌석 예약 해제
    public void removeReservedSeat(String key) {
        reservedSeats.remove(key);
    }


}
