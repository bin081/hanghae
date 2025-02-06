package io.hhplus.concertreservation.api.support.exception;

public class SeatAlreadyReservedException extends ConcertReservationException {
    public SeatAlreadyReservedException() {
        super("The seat is already reserved by another user.", 409); // 409: Conflict
    }
}