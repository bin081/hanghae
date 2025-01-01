package io.hhplus.concert.mockapi.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ReservationResponse {
    private String availableDates;
    private String availableSeats;

    public ReservationResponse(String availableDates, String availableSeats) {
        this.availableDates = availableDates;
        this.availableSeats = availableSeats;
    }

    // Getter and Setter methods
    public String getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(String availableDates) {
        this.availableDates = availableDates;
    }

    public String getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(String availableSeats) {
        this.availableSeats = availableSeats;
    }
}
