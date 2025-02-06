package io.hhplus.concertreservation.api.support.exception;

public class ConcertReservationException extends RuntimeException{
    private final int statusCode;

    public ConcertReservationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
