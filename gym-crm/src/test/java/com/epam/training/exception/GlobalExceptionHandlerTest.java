package com.epam.training.exception;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleUserNotFoundException() {
        UserNotFoundException ex = new UserNotFoundException(1L);
        Map<String, String> response = handler.handleNotFound(ex);
        assertEquals("User with id 1 not found", response.get("error"));
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("First name cannot be null or blank");
        Map<String, String> response = handler.handleBadRequest(ex);
        assertEquals("First name cannot be null or blank", response.get("error"));
    }

    @Test
    void shouldHandleUserNotFoundByMessage() {
        UserNotFoundException ex = new UserNotFoundException("Custom message");
        Map<String, String> response = handler.handleNotFound(ex);
        assertEquals("Custom message", response.get("error"));
    }
}