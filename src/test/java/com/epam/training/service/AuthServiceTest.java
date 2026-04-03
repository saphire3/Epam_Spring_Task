package com.epam.training.service;

import com.epam.training.exception.UserBlockedException;
import com.epam.training.model.Trainee;
import com.epam.training.repository.UserRepository;
import com.epam.training.security.JwtUtil;
import com.epam.training.security.LoginAttemptService;
import com.epam.training.security.TokenBlacklist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() {
        UserDetails userDetails = User.builder()
                .username("john.smith")
                .password("encoded")
                .authorities(Collections.emptyList())
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(loginAttemptService.isBlocked("john.smith")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        String token = authService.login("john.smith", "password");

        assertEquals("jwt-token", token);
        verify(loginAttemptService).loginSucceeded("john.smith");
    }

    @Test
    void shouldThrowWhenUserIsBlocked() {
        when(loginAttemptService.isBlocked("john.smith")).thenReturn(true);

        assertThrows(UserBlockedException.class, () -> authService.login("john.smith", "password"));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void shouldHandleBadCredentials() {
        when(loginAttemptService.isBlocked("john.smith")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login("john.smith", "wrong"));
        verify(loginAttemptService).loginFailed("john.smith");
    }

    @Test
    void shouldLogout() {
        String token = "some.jwt.token";
        Date expiry = new Date(System.currentTimeMillis() + 10000);
        when(jwtUtil.extractExpiration(token)).thenReturn(expiry);

        authService.logout(token);

        verify(tokenBlacklist).blacklist(token, expiry);
    }

    @Test
    void shouldHandleLogoutWithInvalidToken() {
        String token = "invalid.token";
        when(jwtUtil.extractExpiration(token)).thenThrow(new RuntimeException("parse error"));

        // Should not throw
        assertDoesNotThrow(() -> authService.logout(token));
    }

    @Test
    void shouldChangePassword() {
        Trainee user = new Trainee();
        user.setUsername("john.smith");
        user.setPassword("old");

        when(userRepository.findByUsername("john.smith")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encoded");

        authService.changePassword("john.smith", "newPassword");

        assertEquals("encoded", user.getPassword());
        verify(userRepository).save(user);
    }
}
