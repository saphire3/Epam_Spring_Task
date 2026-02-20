package com.epam.training.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void shouldLoadSpringContext() {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        assertNotNull(context);

        context.close();
    }
}