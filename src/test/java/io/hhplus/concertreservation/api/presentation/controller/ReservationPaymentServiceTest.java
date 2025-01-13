package io.hhplus.concertreservation.api.presentation.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concertreservation.api.data.entity.Payment;
import io.hhplus.concertreservation.api.data.repository.PaymentRepository;
import io.hhplus.concertreservation.api.presentation.dto.PaymentRequest;
import io.hhplus.concertreservation.api.service.BalanceService;
import io.hhplus.concertreservation.api.service.PaymentService;
import io.hhplus.concertreservation.api.service.QueueService;
import io.hhplus.concertreservation.api.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationPaymentServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private QueueService queueService;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testProcessPayment_Success() throws Exception {
        // 결제 요청 데이터
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(UUID.randomUUID()); // 유저 ID
        paymentRequest.setToken("someValidToken"); // 대기열 토큰
        paymentRequest.setDate("2025-01-13"); // 예약 날짜
        paymentRequest.setSeatNumber(1); // 좌석 번호
        paymentRequest.setAmount(100.0); // 결제 금액

        // 목 서비스 설정
        when(queueService.isAllowedToReserve(anyString())).thenReturn(true); // 대기열 허용
        when(reservationService.isSeatHeld(anyString(),  anyInt())).thenReturn(true); // 좌석 임시 예약 상태 확인
        when(balanceService.hasSufficientBalance(any(UUID.class), anyDouble())).thenReturn(true); // 잔액 충분 확인
        doNothing().when(balanceService).deductBalance(any(UUID.class), anyDouble()); // 잔액 차감
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment(paymentRequest.getUserId(), paymentRequest.getDate(), paymentRequest.getSeatNumber(), paymentRequest.getAmount())); // 결제 기록 저장

        // API 호출 및 검증
        mockMvc.perform(post("/api/reservation1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest))) // objectMapper 사용
                        .andExpect(status().isOk())
                        .andExpect(content().string("결제 성공 및 좌석 예약 확정"));
    }
/*
    @Test
    public void testProcessPayment_Failure_QueueError() throws Exception {
        // 결제 요청 데이터
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(UUID.randomUUID()); // 유저 ID
        paymentRequest.setToken("someInvalidToken"); // 잘못된 대기열 토큰
        paymentRequest.setDate("2025-01-13"); // 예약 날짜
        paymentRequest.setSeatNumber(1); // 좌석 번호
        paymentRequest.setAmount(100.0); // 결제 금액

        // 목 서비스 설정
        when(queueService.isAllowedToReserve(anyString())).thenReturn(false); // 대기열 허용 실패

        // API 호출 및 검증
        mockMvc.perform(post("/api/reservation1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(paymentRequest)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string("대기열 순서가 아님"));
        }

    @Test
    public void testProcessPayment_Failure_InsufficientBalance() throws Exception {
        // 결제 요청 데이터
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(UUID.randomUUID()); // 유저 ID
        paymentRequest.setToken("someValidToken"); // 대기열 토큰
        paymentRequest.setDate("2025-01-13"); // 예약 날짜
        paymentRequest.setSeatNumber(1); // 좌석 번호
        paymentRequest.setAmount(100.0); // 결제 금액

        // 목 서비스 설정
        when(queueService.isAllowedToReserve(anyString())).thenReturn(true); // 대기열 허용
        when(reservationService.isSeatHeld(anyString(), Integer.parseInt(anyString()))).thenReturn(true); // 좌석 임시 예약 상태 확인
        when(balanceService.hasSufficientBalance(any(UUID.class), anyDouble())).thenReturn(false); // 잔액 부족

        // API 호출 및 검증
        mockMvc.perform(post("/api/reservation1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("잔액 부족"));
    }

    @Test
    public void testProcessPayment_Failure_SeatNotHeld() throws Exception {
        // 결제 요청 데이터
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(UUID.randomUUID()); // 유저 ID
        paymentRequest.setToken("someValidToken"); // 대기열 토큰
        paymentRequest.setDate("2025-01-13"); // 예약 날짜
        paymentRequest.setSeatNumber(1); // 좌석 번호
        paymentRequest.setAmount(100.0); // 결제 금액

        // 목 서비스 설정
        when(queueService.isAllowedToReserve(anyString())).thenReturn(true); // 대기열 허용
        when(reservationService.isSeatHeld(anyString(), Integer.parseInt(anyString()))).thenReturn(false); // 좌석 임시 예약 상태 확인 실패

        // API 호출 및 검증
        mockMvc.perform(post("/api/reservation1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("좌석 예약 시간이 만료되었거나 예약 불가"));
    }*/
}
