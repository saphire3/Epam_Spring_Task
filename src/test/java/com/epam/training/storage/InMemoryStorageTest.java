package com.epam.training.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStorageTest {

    @Test
    void shouldReturnNamespaces() {

        InMemoryStorage storage = new InMemoryStorage();

        assertNotNull(storage.getNamespace("trainees"));
        assertNotNull(storage.getNamespace("trainers"));
        assertNotNull(storage.getNamespace("trainings"));
    }
}