package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.presentation.dto.SeatResponse;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReservationService {

    private final ConcurrentHashMap<String, List<Integer>> availableSeats = new ConcurrentHashMap<>();
    private final List<String> availableDates = new ArrayList<>();

    public ReservationService(){

    }

    public List<String> getAvailableDates(){

        return Collections.unmodifiableList(availableDates);

    }

    public List<SeatResponse> getAvailableSeats(String date){
        List<Integer> seats = availableSeats.getOrDefault(date, new ArrayList<>());
        List<SeatResponse> response = new ArrayList<>();
        for (Integer seatNumber:seats){
            response.add(new SeatResponse(seatNumber));
        }
        return response;
    }

}
