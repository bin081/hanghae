package io.hhplus.concertreservation.api.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concertreservation.api.data.entity.UserQueue;
import io.hhplus.concertreservation.api.data.repository.UserQueueRepository;
import io.hhplus.concertreservation.api.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationTotalController.class)
public class TokenServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private QueueService queueService;

    @MockBean
    private PaymentService paymentService; // 추가

    @MockBean
    private SeatReservationService seatReservationService; // 추가

    @MockBean
    private BalanceService balanceService; // 추가

    @MockBean
    private ReservationService reservationService; // 추가

    @MockBean
    private UserQueueRepository userQueueRepository;

    @Test
    public void testGenerateQueueToken_Success() throws Exception {
        // Mock 데이터 생성
        String mockToken = "test-token";
        UserQueue mockUserQueue = new UserQueue();
        mockUserQueue.setId(1L);
        mockUserQueue.setToken(mockToken);
        mockUserQueue.setEnteredAt(LocalDateTime.now());
        mockUserQueue.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        mockUserQueue.setStatus("WAITING");

        // Mock 동작 설정
        Mockito.when(tokenService.generateToken(eq(1L))).thenReturn(mockToken);
        Mockito.when(queueService.addUserToQueue(eq(mockToken))).thenReturn(mockUserQueue);
        Mockito.when(queueService.isUserEligible(any())).thenReturn(true);

        // API 호출 및 검증
        mockMvc.perform(post("/api/reservation1/generate/token")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(mockUserQueue)));
    }

    @Test
    public void testGenerateQueueToken_NotEligible() throws Exception {
        // Mock 데이터 생성
        String mockToken = "test-token";
        UserQueue mockUserQueue = new UserQueue();
        mockUserQueue.setId(1L);
        mockUserQueue.setToken(mockToken);

        // Mock 동작 설정
        Mockito.when(tokenService.generateToken(eq(1L))).thenReturn(mockToken);
        Mockito.when(queueService.addUserToQueue(eq(mockToken))).thenReturn(mockUserQueue);
        Mockito.when(queueService.isUserEligible(any())).thenReturn(false);

        // API 호출 및 검증
        mockMvc.perform(post("/api/reservation1/generate/token")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("대기열에 있지 않습니다."));
    }
}
