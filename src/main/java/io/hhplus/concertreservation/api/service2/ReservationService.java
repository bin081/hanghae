package io.hhplus.concertreservation.api.service2;

import io.hhplus.concertreservation.api.data.entity2.*;
import io.hhplus.concertreservation.api.data.repository2.*;
import io.hhplus.concertreservation.api.presentation.dto2.SeatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class ReservationService {

    @Autowired
    private UserQueueRepository userQueueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public String generateQueueToken(Long userId) {
        // Generate unique token for user in the queue
        UserQueue userQueue = new UserQueue();
        userQueue.setToken("unique-token-" + userId);
        userQueue.setEnteredAt(LocalDateTime.now());
        userQueue.setExpiredAt(LocalDateTime.now().plusMinutes(5)); // Set expiration to 5 minutes
        userQueue.setStatus("WAITING");
        userQueueRepository.save(userQueue);
        return userQueue.getToken();
    }

    public List<String> getAvailableDates() {
        List<Concert> concerts = concertRepository.findByReservationStartAtBeforeAndReservationEndAtAfter(LocalDateTime.now(), LocalDateTime.now());
        // Generate available dates from concerts
        return concerts.stream()
                .map(concert -> concert.getReservationStartAt().toString())
                .collect(Collectors.toList()); // toList() 대신 collect(Collectors.toList()) 사용
    }


    public List<SeatResponse> getAvailableSeats(String date) {
        List<Seat> seats = seatRepository.findByConcertScheduleIdAndStatus(date, "AVAILABLE");
        return seats.stream()
                .map(seat -> new SeatResponse(seat.getSeatNumber(), seat.getPrice(), seat.getStatus()))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean reserveSeat(Long userId, Long seatId, Double amount) {
        Optional<Seat> seatOptional = seatRepository.findById(seatId);
        if (seatOptional.isEmpty()) {
            return false; // Seat not found
        }

        Seat seat = seatOptional.get();
        if (!seat.getStatus().equals("AVAILABLE")) {
            return false; // Seat not available
        }

        // Lock seat for 5 minutes
        seat.setStatus("TEMPORARY");
        seatRepository.save(seat);

        // Create reservation (without payment)
        Reservation reservation = new Reservation();
        reservation.setUser(userRepository.getById(userId));
        reservation.setSeat(seat);
        reservation.setPrice(amount);
        reservation.setCreateDate(LocalDateTime.now());
        reservation.setUpdateDate(LocalDateTime.now());
        reservation.setExpiredDate(LocalDateTime.now().plusMinutes(5)); // Temporary reservation expiry time
        reservation.setStatus("RESERVED");
        reservationRepository.save(reservation);

        return true;
    }

    public boolean processPayment(Long userId, Long reservationId, Double amount) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isEmpty() || !reservationOptional.get().getUser().getId().equals(userId)) {
            return false; // Reservation not found or user mismatch
        }

        Payment payment = new Payment();
        payment.setUser(userRepository.getById(userId));
        payment.setAmount(amount);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Update reservation status to "PAID"
        Reservation reservation = reservationOptional.get();
        reservation.setStatus("PAID");
        reservation.setUpdateDate(LocalDateTime.now());
        reservationRepository.save(reservation);

        // Update seat status to "SOLD"
        Seat seat = reservation.getSeat();
        seat.setStatus("SOLD");
        seatRepository.save(seat);

        return true;
    }
}
