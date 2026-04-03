package com.epam.training.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_MS = 86400000L;

    @Mock
    private TokenBlacklist tokenBlacklist;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(tokenBlacklist);
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", EXPIRATION_MS);
    }

    private UserDetails userDetails(String username) {
        return User.builder()
                .username(username)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void shouldGenerateToken() {
        UserDetails user = userDetails("john.smith");
        String token = jwtUtil.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractUsername() {
        UserDetails user = userDetails("john.smith");
        String token = jwtUtil.generateToken(user);
        assertEquals("john.smith", jwtUtil.extractUsername(token));
    }

    @Test
    void shouldExtractExpiration() {
        UserDetails user = userDetails("john.smith");
        String token = jwtUtil.generateToken(user);
        Date expiry = jwtUtil.extractExpiration(token);
        assertNotNull(expiry);
        assertTrue(expiry.after(new Date()));
    }

    @Test
    void shouldValidateToken() {
        UserDetails user = userDetails("john.smith");
        String token = jwtUtil.generateToken(user);
        when(tokenBlacklist.isBlacklisted(token)).thenReturn(false);
        assertTrue(jwtUtil.validateToken(token, user));
    }

    @Test
    void shouldReturnFalseForBlacklistedToken() {
        UserDetails user = userDetails("john.smith");
        String token = jwtUtil.generateToken(user);
        when(tokenBlacklist.isBlacklisted(token)).thenReturn(true);
        assertFalse(jwtUtil.validateToken(token, user));
    }

    @Test
    void shouldReturnFalseForWrongUser() {
        UserDetails user1 = userDetails("john.smith");
        UserDetails user2 = userDetails("jane.doe");
        String token = jwtUtil.generateToken(user1);
        // The username doesn't match, so validation fails before checking blacklist
        // We still need the mock to not throw - lenient avoids unnecessary stubbing error
        lenient().when(tokenBlacklist.isBlacklisted(token)).thenReturn(false);
        assertFalse(jwtUtil.validateToken(token, user2));
    }

    @Test
    void shouldReturnFalseForExpiredToken() {
        JwtUtil shortLivedJwtUtil = new JwtUtil(tokenBlacklist);
        ReflectionTestUtils.setField(shortLivedJwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(shortLivedJwtUtil, "expirationMs", -1000L);

        UserDetails user = userDetails("john.smith");
        String token = shortLivedJwtUtil.generateToken(user);
        assertTrue(shortLivedJwtUtil.isTokenExpired(token));
    }

    @Test
    void shouldReturnFalseOnMalformedToken() {
        UserDetails user = userDetails("john.smith");
        assertFalse(jwtUtil.validateToken("not.a.valid.token", user));
    }
}
