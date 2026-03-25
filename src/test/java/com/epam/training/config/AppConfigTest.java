package com.epam.training.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void propertyConfigurer_returnsBean() {
        PropertySourcesPlaceholderConfigurer bean = AppConfig.propertyConfigurer();
        assertNotNull(bean);
    }
}