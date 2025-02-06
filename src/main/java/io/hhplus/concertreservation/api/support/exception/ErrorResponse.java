package io.hhplus.concertreservation.api.support.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String error;

    public ErrorResponse(String error, int value) {
        this.error = error;
    }

    // Getter
}
