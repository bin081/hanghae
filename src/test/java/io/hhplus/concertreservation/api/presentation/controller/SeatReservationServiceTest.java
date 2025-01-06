package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.data.repository.ReservationRepository;
import io.hhplus.concertreservation.api.service.QueueService;
import io.hhplus.concertreservation.api.service.SeatReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SeatReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private QueueService queueService;

    @InjectMocks
    private SeatReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReserveSeat_Success() {
        // Arrange
        String userToken = "user123";
        String date = "2025-01-06";
        int seatNumber = 1;
        String key = date + "-" + seatNumber;

        when(queueService.isUserEligible(userToken)).thenReturn(true);
        when(reservationRepository.isSeatReserved(key)).thenReturn(false);

        // Act
        boolean result = reservationService.reserveSeat(userToken, date, seatNumber);

        // Assert
        assertTrue(result);  // 예약이 성공해야 한다
        verify(reservationRepository).reserveSeat(key, userToken);  // 좌석 예약이 호출되었는지 확인
    }

    @Test
    void testReserveSeat_SeatAlreadyReserved() {
        // Arrange
        String userToken = "user123";
        String date = "2025-01-06";
        int seatNumber = 1;
        String key = date + "-" + seatNumber;

        when(queueService.isUserEligible(userToken)).thenReturn(true);
        when(reservationRepository.isSeatReserved(key)).thenReturn(true);

        // Act
        boolean result = reservationService.reserveSeat(userToken, date, seatNumber);

        // Assert
        assertFalse(result);  // 좌석이 이미 예약된 경우 예약 실패
        verify(reservationRepository, never()).reserveSeat(anyString(), anyString());  // 예약 호출되지 않음
    }

    @Test
    void testReserveSeat_UserNotEligible() {
        // Arrange
        String userToken = "user123";
        String date = "2025-01-06";
        int seatNumber = 1;

        when(queueService.isUserEligible(userToken)).thenReturn(false);

        // Act
        boolean result = reservationService.reserveSeat(userToken, date, seatNumber);

        // Assert
        assertFalse(result);  // 대기열 검증 실패로 예약이 실패해야 한다
    }
}
