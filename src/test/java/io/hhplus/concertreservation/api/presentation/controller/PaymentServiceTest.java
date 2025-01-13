package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.presentation.dto.BalanceResponse;
import io.hhplus.concertreservation.api.service.BalanceService;
import io.hhplus.concertreservation.api.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
public class PaymentServiceTest {
    @Mock
    private BalanceService balanceService;

    @Mock
    private ReservationService reservationService;  // Mocking the ReservationService

    @InjectMocks
    private ReservationTotalController reservationTotalController;  // InjectMocks to inject mocks into this class

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Make sure mocks are initialized
        mockMvc = MockMvcBuilders.standaloneSetup(reservationTotalController).build();
    }

    @Test
    void testChargeAmount_Success() throws Exception {
        // given
        Long userId = 1L;
        Long reservationId = 1L;
        Double amount = 100.0;

        when(reservationService.processPayment(userId, reservationId, amount)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/api/reservation1/charge/amount")
                        .param("userId", String.valueOf(userId))
                        .param("reservationId", String.valueOf(reservationId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment successful"));

        verify(reservationService, times(1)).processPayment(userId, reservationId, amount);
    }
    @Test
    void testChargeAmount_Failure() throws Exception {
        // given
        Long userId = 1L;
        Long reservationId = 1L;
        Double amount = 100.0;

        when(reservationService.processPayment(userId, reservationId, amount)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/api/reservation1/charge/amount")
                        .param("userId", String.valueOf(userId))
                        .param("reservationId", String.valueOf(reservationId))
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Payment failed"));

        verify(reservationService, times(1)).processPayment(userId, reservationId, amount);
    }


}
