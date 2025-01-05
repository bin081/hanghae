package io.hhplus.concertreservation.api.presentation.controller;

import io.hhplus.concertreservation.api.presentation.dto.UserTokenReponseDto;
import io.hhplus.concertreservation.api.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    /*대기열 토큰 발급과 검증을 처리하는 API 앤드포인트*/
    @Autowired
    private final TokenService tokenService;
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/generate")
    public UserTokenReponseDto generateToken(@RequestBody String uuid){
        return tokenService.generateToken(uuid);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String token){
        return tokenService.validateToken(token);
    }
}
