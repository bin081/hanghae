package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.entity.*;
import io.hhplus.concertreservation.api.data.repository.*;
import io.hhplus.concertreservation.api.presentation.dto.SeatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    private final ConcurrentHashMap<String, Long> tokenStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> waitList = new ConcurrentHashMap<>();

    public String generateQueueToken(Long userId) {
        // Generate unique token for user in the queue
        UserQueue userQueue = new UserQueue();
        String token = UUID.randomUUID().toString();
        userQueue.setToken(token + "-unique-token-" + userId);
        int positionInQueue = waitList.size()+1;
        tokenStorage.put(token, userId);
        waitList.put(userId, positionInQueue);
        log.info("token : ", token , ",  positionInQueue : " , positionInQueue);
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


    private final Map<String, Map<Integer, UUID>> reservations = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Long>> reservationTimes = new ConcurrentHashMap<>();

    private static final long RESERVATION_TIMEOUT = 5 * 60 * 1000; // 5분

    public synchronized boolean isSeatHeld(String date, int seatNumber) {
        if (!reservations.containsKey(date) || !reservations.get(date).containsKey(seatNumber)) {
            return false;
        }
        long reservedTime = reservationTimes.get(date).get(seatNumber);
        return System.currentTimeMillis() - reservedTime < RESERVATION_TIMEOUT;
    }

    public synchronized void holdSeat(String date, int seatNumber, UUID userId) {
        reservations.computeIfAbsent(date, k -> new ConcurrentHashMap<>()).put(seatNumber, userId);
        reservationTimes.computeIfAbsent(date, k -> new ConcurrentHashMap<>()).put(seatNumber, System.currentTimeMillis());
    }

    public synchronized void confirmReservation(String date, int seatNumber, UUID userId) {
        if (isSeatHeld(date, seatNumber)) {
            reservations.get(date).put(seatNumber, userId);
            reservationTimes.get(date).put(seatNumber, System.currentTimeMillis());
        } else {
            throw new IllegalStateException("예약 시간이 초과되었습니다.");
        }
    }

    public synchronized void releaseSeat(String date, int seatNumber) {
        if (reservations.containsKey(date)) {
            reservations.get(date).remove(seatNumber);
            reservationTimes.get(date).remove(seatNumber);
        }
    }
}
