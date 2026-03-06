package com.epam.training;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ApplicationTest {

    @Test
    void shouldRunMainWithoutCrashing() {
        assertDoesNotThrow(() -> Application.main(new String[]{}));
    }
}