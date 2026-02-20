package com.epam.training.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageInitializerTest {

    @Test
    void shouldInitializeWithoutCrashing() {

        InMemoryStorage storage = new InMemoryStorage();
        StorageInitializer initializer = new StorageInitializer(storage);

        assertDoesNotThrow(() -> initializer.afterPropertiesSet());
    }
    @Test
    void shouldHandleMissingFileGracefully() {

        InMemoryStorage storage = new InMemoryStorage();
        StorageInitializer initializer = new StorageInitializer(storage);

        assertDoesNotThrow(() -> initializer.afterPropertiesSet());
    }
}