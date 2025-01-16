package io.hhplus.concertreservation.api.support.exception;

public class TokenValidationException extends ConcertReservationException {
    public TokenValidationException() {
        super("Invalid or expired user token.", 401); // 401: Unauthorized
    }
}