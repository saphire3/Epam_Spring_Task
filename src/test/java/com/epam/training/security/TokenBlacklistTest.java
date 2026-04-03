package com.epam.training.security;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistTest {

    private final TokenBlacklist tokenBlacklist = new TokenBlacklist();

    @Test
    void shouldNotBeBlacklistedInitially() {
        assertFalse(tokenBlacklist.isBlacklisted("some.token"));
    }

    @Test
    void shouldBlacklistToken() {
        Date expiry = new Date(System.currentTimeMillis() + 10000);
        tokenBlacklist.blacklist("my.token", expiry);
        assertTrue(tokenBlacklist.isBlacklisted("my.token"));
    }

    @Test
    void shouldCleanupExpiredTokens() {
        Date expiredDate = new Date(System.currentTimeMillis() - 1000);
        tokenBlacklist.blacklist("expired.token", expiredDate);

        tokenBlacklist.cleanupExpiredTokens();

        assertFalse(tokenBlacklist.isBlacklisted("expired.token"));
    }

    @Test
    void shouldNotRemoveNonExpiredTokensDuringCleanup() {
        Date futureDate = new Date(System.currentTimeMillis() + 60000);
        tokenBlacklist.blacklist("valid.token", futureDate);

        tokenBlacklist.cleanupExpiredTokens();

        assertTrue(tokenBlacklist.isBlacklisted("valid.token"));
    }
}
