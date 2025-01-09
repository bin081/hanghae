package io.hhplus.concertreservation.api.support.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class TokenUtil {

    private static final String SECRET_KEY = "your_secret_key"; // 비밀 키 (환경 변수로 관리 권장)

    // JWT에서 userId 추출
    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();

            return Long.valueOf(claims.getSubject()); // subject를 userId로 사용
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Invalid token");
        } catch (Exception e) {
            throw new IllegalArgumentException("Token parsing error");
        }
    }

    // 토큰 유효성 검증
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token.replace("Bearer ", ""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
