package com.epam.training.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private final PasswordGenerator generator = new PasswordGenerator();

    @Test
    void shouldGeneratePasswordWithLength10() {
        String password = generator.generate();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void shouldGenerateDifferentPasswords() {
        String first = generator.generate();
        String second = generator.generate();

        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first, second);
    }
}