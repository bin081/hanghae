package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.presentation.dto.UserTokenReponseDto;
import io.hhplus.concertreservation.api.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenControllerTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TokenController tokenController;


    @Test
    void testGenerateToken() {
        String uuid = "user-12345";
        UserTokenReponseDto tokenResponse = new UserTokenReponseDto("mock-token", 1);

        when(tokenService.generateToken(uuid)).thenReturn(tokenResponse);

        UserTokenReponseDto result = tokenController.generateToken(uuid);

        // 검증
        assertNotNull(result); // 결과 객체가 null이 아님을 확인
        assertNotNull(result.getToken()); // 토큰 값이 null이 아님을 확인
        assertEquals("mock-token", result.getToken()); // 반환된 토큰 값 검증
        assertEquals(1, result.getQueuePosition()); // 대기열 위치가 1인지 검증

        // tokenService.generateToken 호출 검증
        verify(tokenService, times(1)).generateToken(uuid);
    }


    @Test
    void testValidateToken() {
        String validToken = "mock-token";

        // Mockito로 TokenService의 validateToken 메서드 호출 시 mock 반환값 설정
        when(tokenService.validateToken(validToken)).thenReturn(true);

        // Controller 메서드 호출
        boolean isValid = tokenController.validateToken(validToken);

        // 검증
        assertTrue(isValid);

        // TokenService의 validateToken 메서드가 호출되었는지 검증
        verify(tokenService, times(1)).validateToken(validToken);
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid-token";

        // Mockito로 TokenService의 validateToken 메서드 호출 시 mock 반환값 설정
        when(tokenService.validateToken(invalidToken)).thenReturn(false);

        // Controller 메서드 호출
        boolean isValid = tokenController.validateToken(invalidToken);

        // 검증
        assertFalse(isValid);

        // TokenService의 validateToken 메서드가 호출되었는지 검증
        verify(tokenService, times(1)).validateToken(invalidToken);
    }
}
