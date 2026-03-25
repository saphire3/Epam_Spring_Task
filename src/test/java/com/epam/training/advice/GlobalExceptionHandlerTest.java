package com.epam.training.advice;

import com.epam.training.dto.response.ApiErrorResponse;
import com.epam.training.exception.AuthenticationException;
import com.epam.training.exception.BadRequestException;
import com.epam.training.exception.ConflictException;
import com.epam.training.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/john");
        MDC.put("transactionId", "tx-1");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleNotFound(new UserNotFoundException("User not found"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals("/api/users/john", response.getBody().getPath());
        assertEquals("tx-1", response.getBody().getTransactionId());

        MDC.clear();
    }

    @Test
    void handleAuth_returns401() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/auth/login");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleAuth(new AuthenticationException("Invalid"), request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid", response.getBody().getMessage());
    }

    @Test
    void handleConflict_returns409() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleConflict(new ConflictException("Conflict"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict", response.getBody().getMessage());
    }

    @Test
    void handleBadRequest_returns400() {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/test");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleBadRequest(new BadRequestException("Bad request"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().getMessage());
    }

    @Test
    void handleGeneric_returns500() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleGeneric(new RuntimeException("Boom"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Boom", response.getBody().getMessage());
    }
}