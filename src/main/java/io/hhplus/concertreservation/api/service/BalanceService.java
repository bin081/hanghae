package io.hhplus.concertreservation.api.service;

import io.hhplus.concertreservation.api.data.entity.Users;
import io.hhplus.concertreservation.api.data.repository.UserRepository;
import io.hhplus.concertreservation.api.presentation.dto.BalanceResponse;
import io.hhplus.concertreservation.api.support.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BalanceService {

    private final UserRepository userRepository;

    public BalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    private final Map<UUID, Double> userBalances = new ConcurrentHashMap<>();

    public boolean hasSufficientBalance(UUID userId, double amount) {
        return userBalances.getOrDefault(userId, 0.0) >= amount;
    }

    public void deductBalance(UUID userId, double amount) {
        userBalances.computeIfPresent(userId, (id, balance) -> balance - amount);
    }

    public void addBalance(UUID userId, double amount) {
        userBalances.merge(userId, amount, Double::sum);
    }

    public double getBalance(Long userId) {
        return userBalances.getOrDefault(userId, 0.0);
    }

    public BalanceResponse getUserBalance(Long userId) throws UserNotFoundException  {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Users not found"));

        return new BalanceResponse(user.getId(), user.getBalance(), "USD");
    }
}
