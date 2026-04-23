package com.epam.training.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private final PasswordGenerator generator = new PasswordGenerator();

    @Test
    void shouldGeneratePasswordWithLength10() {
        String password = generator.generate();
        assertEquals(10, password.length());
    }
}
