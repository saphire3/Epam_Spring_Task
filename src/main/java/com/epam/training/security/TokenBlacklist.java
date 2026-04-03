package com.epam.training.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklist.class);

    // token -> expiry date
    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, Date expiresAt) {
        blacklistedTokens.put(token, expiresAt);
        log.info("Token blacklisted, expires at: {}", expiresAt);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    @Scheduled(fixedRate = 3_600_000)
    public void cleanupExpiredTokens() {
        Date now = new Date();
        Set<String> tokensToRemove = new java.util.HashSet<>();
        blacklistedTokens.forEach((token, expiry) -> {
            if (expiry.before(now)) {
                tokensToRemove.add(token);
            }
        });
        tokensToRemove.forEach(blacklistedTokens::remove);
        if (!tokensToRemove.isEmpty()) {
            log.info("Cleaned up {} expired blacklisted tokens", tokensToRemove.size());
        }
    }
}
