package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.presentation.dto.SeatResponse;
import io.hhplus.concertreservation.api.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAvailableDates() {
        // Given
        List<String> mockDates = Arrays.asList("2024-01-10", "2024-01-11");
        when(reservationService.getAvailableDates()).thenReturn(mockDates);

        // When
        List<String> result = reservationController.getAvailableDates();

        // Then
        assertNotNull(result); // 결과가 null이 아님을 검증
        assertEquals(2, result.size()); // 날짜 개수 검증
        assertEquals("2024-01-10", result.get(0)); // 첫 번째 날짜 검증
        assertEquals("2024-01-11", result.get(1)); // 두 번째 날짜 검증

        verify(reservationService, times(1)).getAvailableDates(); // 서비스 메소드가 1번 호출되었는지 검증
    }

    @Test
    void testGetAvailableSeats() {
        // Given
        String testDate = "2024-01-10";
        List<SeatResponse> mockSeats = Arrays.asList(new SeatResponse(1), new SeatResponse(2));
        when(reservationService.getAvailableSeats(testDate)).thenReturn(mockSeats);

        // When
        List<SeatResponse> result = reservationController.getAvailableSeats(testDate);

        // Then
        assertNotNull(result); // 결과가 null이 아님을 검증
        assertEquals(2, result.size()); // 좌석 개수 검증
        assertEquals(1, result.get(0).getSeatNumber()); // 첫 번째 좌석 번호 검증
        assertEquals(2, result.get(1).getSeatNumber()); // 두 번째 좌석 번호 검증

        verify(reservationService, times(1)).getAvailableSeats(testDate); // 서비스 메소드가 1번 호출되었는지 검증
    }

    @Test
    void testGetAvailableSeats_NoSeatsAvailable() {
        // Given
        String testDate = "2024-01-10";
        when(reservationService.getAvailableSeats(testDate)).thenReturn(Collections.emptyList());

        // When
        List<SeatResponse> result = reservationController.getAvailableSeats(testDate);

        // Then
        assertNotNull(result); // 결과가 null이 아님을 검증
        assertTrue(result.isEmpty()); // 좌석이 없는 경우 검증

        verify(reservationService, times(1)).getAvailableSeats(testDate); // 서비스 메소드가 1번 호출되었는지 검증
    }
}