package com.epam.training.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
        ReflectionTestUtils.setField(loginAttemptService, "maxAttempts", 3);
        ReflectionTestUtils.setField(loginAttemptService, "blockDurationMinutes", 5);
    }

    @Test
    void shouldNotBeBlockedInitially() {
        assertFalse(loginAttemptService.isBlocked("user1"));
    }

    @Test
    void shouldBlockAfterMaxAttempts() {
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");
        assertFalse(loginAttemptService.isBlocked("user1"));

        loginAttemptService.loginFailed("user1");
        assertTrue(loginAttemptService.isBlocked("user1"));
    }

    @Test
    void shouldClearBlockOnSuccess() {
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");
        assertTrue(loginAttemptService.isBlocked("user1"));

        loginAttemptService.loginSucceeded("user1");
        assertFalse(loginAttemptService.isBlocked("user1"));
    }

    @Test
    void shouldClearAttemptsOnSuccess() {
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginSucceeded("user1");

        // After success, should not be blocked even on 2 more failures
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");
        assertFalse(loginAttemptService.isBlocked("user1"));
    }

    @Test
    void shouldHandleMultipleUsersIndependently() {
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");

        assertTrue(loginAttemptService.isBlocked("user1"));
        assertFalse(loginAttemptService.isBlocked("user2"));
    }

    @Test
    void shouldAutoUnblockAfterDurationExpires() throws InterruptedException {
        // Set very short block duration
        ReflectionTestUtils.setField(loginAttemptService, "blockDurationMinutes", 0);
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");
        loginAttemptService.loginFailed("user1");

        // With 0 minutes, block should immediately expire
        Thread.sleep(10);
        assertFalse(loginAttemptService.isBlocked("user1"));
    }
}
