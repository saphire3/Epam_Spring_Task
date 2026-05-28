package com.epam.training.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    private static final String SECRET =
            "bXlTdXBlclNlY3JldEtleUZvckp3dFRva2VuR2VuZXJhdGlvbjEyMzQ1Njc4OTA=";

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secret", SECRET);
        ReflectionTestUtils.setField(provider, "expirationMs", 3_600_000L);
    }

    @Test
    void generateServiceToken_returnsNonNullToken() {
        String token = provider.generateServiceToken();
        assertNotNull(token);
        assertThat(token).contains(".");
    }

    @Test
    void generateServiceToken_producesValidJwt() {
        String token = provider.generateServiceToken();
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    void generateServiceToken_subjectIsGymCrmService() {
        String token = provider.generateServiceToken();
        String payload = token.split("\\.")[1];
        String decoded = new String(java.util.Base64.getUrlDecoder().decode(payload));
        assertThat(decoded).contains("gym-crm-service");
    }
}