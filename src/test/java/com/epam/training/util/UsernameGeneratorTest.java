package com.epam.training.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsernameGeneratorTest {

    private final UsernameGenerator generator = new UsernameGenerator();

    @Test
    void shouldGenerateSimpleUsername() {
        String result = generator.generate("John", "Smith", Set.of());
        assertEquals("John.Smith", result);
    }

    @Test
    void shouldAddSuffixIfDuplicateExists() {
        String result = generator.generate(
                "John",
                "Smith",
                Set.of("John.Smith")
        );
        assertEquals("John.Smith1", result);
    }
}
