package com.epam.training.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void shouldHaveEnableSchedulingAnnotation() {
        EnableScheduling annotation = AppConfig.class.getAnnotation(EnableScheduling.class);
        assertNotNull(annotation);
    }
}
