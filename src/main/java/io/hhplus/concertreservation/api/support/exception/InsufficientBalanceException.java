package io.hhplus.concertreservation.api.support.exception;

public class InsufficientBalanceException extends ConcertReservationException {
    public InsufficientBalanceException() {
        super("User has insufficient balance to make the reservation.", 402); // 402: Payment Required
    }
}