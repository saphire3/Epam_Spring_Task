package com.epam.training.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    @Value("${app.brute-force.max-attempts}")
    private int maxAttempts;

    @Value("${app.brute-force.block-duration-minutes}")
    private int blockDurationMinutes;

    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> blockedUntil = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        int attempts = attemptsCache.merge(username, 1, Integer::sum);
        if (attempts >= maxAttempts) {
            LocalDateTime blockExpiry = LocalDateTime.now().plusMinutes(blockDurationMinutes);
            blockedUntil.put(username, blockExpiry);
            log.warn("Login attempt blocked for user: {}", username);
        }
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockedUntil.remove(username);
        log.info("Login succeeded for user: {}", username);
    }

    public boolean isBlocked(String username) {
        LocalDateTime blockTime = blockedUntil.get(username);
        if (blockTime == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(blockTime)) {
            blockedUntil.remove(username);
            attemptsCache.remove(username);
            log.info("Block expired for user: {}", username);
            return false;
        }
        return true;
    }
}
