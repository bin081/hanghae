package io.hhplus.concertreservation.api.support.exception;


public class ReservationTimeoutException extends ConcertReservationException {
    public ReservationTimeoutException() {
        super("Reservation timed out. The seat is no longer reserved.", 408); // 408: Request Timeout
    }
}