package com.epam.workload.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenValidatorTest {

    private JwtTokenValidator validator;

    private static final String SECRET =
            "bXlTdXBlclNlY3JldEtleUZvckp3dFRva2VuR2VuZXJhdGlvbjEyMzQ1Njc4OTA=";

    @BeforeEach
    void setUp() {
        validator = new JwtTokenValidator();
        ReflectionTestUtils.setField(validator, "secret", SECRET);
    }

    private String buildToken(long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
        return Jwts.builder()
                .subject("test-service")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    @Test
    void isValid_validToken_returnsTrue() {
        assertTrue(validator.isValid(buildToken(3_600_000L)));
    }

    @Test
    void isValid_expiredToken_returnsFalse() {
        assertFalse(validator.isValid(buildToken(-1_000L)));
    }

    @Test
    void isValid_garbageToken_returnsFalse() {
        assertFalse(validator.isValid("not.a.valid.jwt"));
    }

    @Test
    void isValid_emptyString_returnsFalse() {
        assertFalse(validator.isValid(""));
    }

    @Test
    void validateToken_validToken_returnsCorrectSubject() {
        String token = buildToken(3_600_000L);
        Claims claims = validator.validateToken(token);
        assertEquals("test-service", claims.getSubject());
    }

    @Test
    void validateToken_expiredToken_throwsException() {
        String token = buildToken(-1_000L);
        assertThrows(Exception.class, () -> validator.validateToken(token));
    }
}