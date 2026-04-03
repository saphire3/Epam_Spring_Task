package com.epam.training.service;

import com.epam.training.exception.UserBlockedException;
import com.epam.training.model.User;
import com.epam.training.repository.UserRepository;
import com.epam.training.security.JwtUtil;
import com.epam.training.security.LoginAttemptService;
import com.epam.training.security.TokenBlacklist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklist tokenBlacklist;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       LoginAttemptService loginAttemptService,
                       TokenBlacklist tokenBlacklist,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
        this.tokenBlacklist = tokenBlacklist;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String username, String password) {
        if (loginAttemptService.isBlocked(username)) {
            log.warn("Login attempt blocked for user: {}", username);
            throw new UserBlockedException(username);
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            loginAttemptService.loginSucceeded(username);
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            log.info("User logged in successfully: {}", username);
            return token;
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(username);
            log.warn("Failed login attempt for user: {}", username);
            throw e;
        }
    }

    public void logout(String token) {
        try {
            Date expiry = jwtUtil.extractExpiration(token);
            tokenBlacklist.blacklist(token, expiry);
            log.info("User logged out, token invalidated");
        } catch (Exception e) {
            log.warn("Failed to blacklist token during logout: {}", e.getMessage());
        }
    }

    public void changePassword(String username, String newRawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
    }
}
