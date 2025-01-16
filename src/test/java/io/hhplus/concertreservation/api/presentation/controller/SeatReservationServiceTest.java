package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.data.entity.UserQueue;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import io.hhplus.concertreservation.api.service.QueueService;
import io.hhplus.concertreservation.api.service.SeatReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SeatReservationServiceTest {
    @Mock
    private UserQueueRepository userQueueRepository;

    @Mock
    private QueueService queueService;

    @InjectMocks
    private SeatReservationService seatReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReserveSeat_Success() {
        // given
        String userToken = "valid-token";
        String date = "2025-01-15";
        int seatNumber = 1;
        String key = date + "-" + seatNumber;

        UserQueue userQueue = new UserQueue();
        when(userQueueRepository.findByToken(userToken)).thenReturn(Optional.of(userQueue));
        when(queueService.isUserEligible(Optional.of(userQueue))).thenReturn(true);

        // when
        boolean result = seatReservationService.reserveSeat(userToken, LocalDate.from(LocalDateTime.parse(date)), seatNumber);

        // then
        assertTrue(result);
        assertTrue(seatReservationService.isSeatReserved(key));
        verify(userQueueRepository, times(1)).findByToken(userToken);
        verify(queueService, times(1)).isUserEligible(Optional.of(userQueue));
    }

    @Test
    void testReserveSeat_Fail_SeatAlreadyReserved() {
        // given
        String userToken = "valid-token";
        String date = "2025-01-15";
        int seatNumber = 1;
        String key = date + "-" + seatNumber;

        UserQueue userQueue = new UserQueue();
        when(userQueueRepository.findByToken(userToken)).thenReturn(Optional.of(userQueue));
        when(queueService.isUserEligible(Optional.of(userQueue))).thenReturn(true);

        // Pre-reserve the seat
        seatReservationService.reserveSeat(userToken, LocalDate.from(LocalDateTime.parse(date)), seatNumber);

        // Reset mock 호출 기록
        reset(userQueueRepository, queueService);
        when(userQueueRepository.findByToken(userToken)).thenReturn(Optional.of(userQueue));

        // when
        boolean result = seatReservationService.reserveSeat(userToken, LocalDate.from(LocalDateTime.parse(date)), seatNumber);

        // then
        assertFalse(result);
        verify(userQueueRepository, times(1)).findByToken(userToken);
        verify(queueService, times(1)).isUserEligible(Optional.of(userQueue));
    }


    @Test
    void testReserveSeat_Fail_UserNotEligible() {
        // given
        String userToken = "invalid-token";
        String date = "2025-01-15";
        int seatNumber = 1;

        when(userQueueRepository.findByToken(userToken)).thenReturn(Optional.empty());

        // when
        boolean result = seatReservationService.reserveSeat(userToken, LocalDate.from(LocalDateTime.parse(date)), seatNumber);

        // then
        assertFalse(result);
        verify(userQueueRepository, times(1)).findByToken(userToken);
        verify(queueService, never()).isUserEligible(any());
    }

    @Test
    void testRemoveReservedSeat() {
        // given
        String key = "2025-01-15-1";
        String userToken = "valid-token";
        int seatNumber = 1;
        seatReservationService.reserveSeat(userToken, LocalDate.from(LocalDateTime.parse(key)), seatNumber);

        // when
        seatReservationService.removeReservedSeat(key);

        // then
        assertFalse(seatReservationService.isSeatReserved(key));
    }
}
