package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import io.hhplus.concertreservation.api.presentation.dto.SeatResponse;
import io.hhplus.concertreservation.api.service.ReservationService;
import io.hhplus.concertreservation.api.service.SeatReservationService;
import io.hhplus.concertreservation.api.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
@ExtendWith(MockitoExtension.class)
public class ConcertSeatServiceTestEntityConcurrencyEntityApi {

    @Mock
    private ReservationService reservationService; // reservationService 모킹

    @InjectMocks
    private ReservationTotalController reservationTotalController; // 테스트 대상 컨트롤러
    @Mock
    private SeatReservationService seatReservationService;

    @Mock
    private QueueService queueService;

    @Mock
    private UserQueueRepository userQueueRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // Mock 객체 초기화
        mockMvc = MockMvcBuilders.standaloneSetup(reservationTotalController).build(); // MockMvc 초기화
    }

    @Test
    void testGetAvailableSeats() throws Exception {
        // given
        String date = "2025-01-15";
        List<SeatResponse> seats = Arrays.asList(
                new SeatResponse(1L, 100.0, "AVAILABLE"),  // Long, Double, String 값으로 설정
                new SeatResponse(2L, 120.0, "AVAILABLE")
        );

        // reservationService의 getAvailableSeats 메소드가 호출될 때, 미리 정의된 seats를 반환하도록 설정
        when(reservationService.getAvailableSeats(date)).thenReturn(seats);

        // when & then
        mockMvc.perform(get("/api/reservation1/seats")
                        .param("date", date) // 날짜 파라미터 전달
                        .contentType(MediaType.APPLICATION_JSON)) // 요청의 콘텐츠 타입 설정
                .andExpect(status().isOk()) // 응답 상태가 200 OK인지 확인
                .andExpect(jsonPath("$[0].seatNum").value(1)) // 첫 번째 좌석의 seatNum이 1인지 확인
                .andExpect(jsonPath("$[0].price").value(100.0)) // 첫 번째 좌석의 price가 100.0인지 확인
                .andExpect(jsonPath("$[0].status").value("AVAILABLE")) // 첫 번째 좌석의 status가 "AVAILABLE"인지 확인
                .andExpect(jsonPath("$[1].seatNum").value(2)) // 두 번째 좌석의 seatNum이 2인지 확인
                .andExpect(jsonPath("$[1].price").value(120.0)) // 두 번째 좌석의 price가 120.0인지 확인
                .andExpect(jsonPath("$[1].status").value("AVAILABLE")); // 두 번째 좌석의 status가 "AVAILABLE"인지 확인

        // verify
        verify(reservationService, times(1)).getAvailableSeats(date); // getAvailableSeats 메소드가 정확히 한 번 호출되었는지 검증
    }

    @Test
    void testGetAvailableDates() throws Exception {
        // given
        List<String> availableDates = Arrays.asList("2025-01-15", "2025-01-16", "2025-01-17");
        when(reservationService.getAvailableDates()).thenReturn(availableDates);

        // when & then
        mockMvc.perform(get("/api/reservation1/dates")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("2025-01-15"))
                .andExpect(jsonPath("$[1]").value("2025-01-16"))
                .andExpect(jsonPath("$[2]").value("2025-01-17"));

        verify(reservationService, times(1)).getAvailableDates();
    }

    @Test
    void testReserveSeat_Fail_UserNotEligible() {
        // given
        String userToken = "invalid-token";
        String date = "2025-01-15";
        int seatNumber = 1;

        // userQueueRepository에서 Optional.empty()를 반환
        when(userQueueRepository.findByToken(userToken)).thenReturn(Optional.empty());

        // when
        boolean result = seatReservationService.reserveSeat(userToken, LocalDate.from(LocalDateTime.parse(date)), seatNumber);

        // then
        assertFalse(result);

        // queueService.isUserEligible()가 호출되지 않았는지 확인
        verify(queueService, never()).isUserEligible(any());
    }

}
