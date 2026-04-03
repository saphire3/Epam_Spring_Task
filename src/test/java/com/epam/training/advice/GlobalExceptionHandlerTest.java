package com.epam.training.advice;

import com.epam.training.exception.TrainingTypeNotFoundException;
import com.epam.training.exception.UserBlockedException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.security.JwtAuthenticationFilter;
import com.epam.training.security.SecurityConfig;
import com.epam.training.controller.AuthController;
import com.epam.training.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
        })
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private com.epam.training.security.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private com.epam.training.security.JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(inv -> {
            ((FilterChain) inv.getArgument(2)).doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    private String loginJson(String user, String pass) throws Exception {
        return objectMapper.writeValueAsString(
                new com.epam.training.dto.request.LoginRequest(user, pass));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(authService.login(any(), any())).thenThrow(new UserNotFoundException("john.smith not found"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("john.smith", "pass")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenUsernameNotFound() throws Exception {
        when(authService.login(any(), any())).thenThrow(new UsernameNotFoundException("not found"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("john.smith", "pass")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenTrainingTypeNotFound() throws Exception {
        when(authService.login(any(), any())).thenThrow(new TrainingTypeNotFoundException("CARDIO"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("john.smith", "pass")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400OnIllegalArgument() throws Exception {
        when(authService.login(any(), any())).thenThrow(new IllegalArgumentException("invalid"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("john.smith", "pass")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500OnGenericException() throws Exception {
        when(authService.login(any(), any())).thenThrow(new RuntimeException("unexpected"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("john.smith", "pass")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn429WhenUserBlocked() throws Exception {
        when(authService.login(any(), any())).thenThrow(new UserBlockedException("john.smith"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("john.smith", "pass")))
                .andExpect(status().isTooManyRequests());
    }
}