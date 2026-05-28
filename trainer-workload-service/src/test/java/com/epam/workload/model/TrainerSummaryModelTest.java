package com.epam.workload.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainerSummaryModelTest {

    @Test
    void noArgConstructor_andSetters() {
        TrainerSummary summary = new TrainerSummary();
        summary.setUsername("john.doe");
        summary.setFirstName("John");
        summary.setLastName("Doe");
        summary.setActive(true);
        Map<Integer, Map<Integer, Integer>> years = new HashMap<>();
        summary.setYears(years);

        assertEquals("john.doe", summary.getUsername());
        assertEquals("John", summary.getFirstName());
        assertEquals("Doe", summary.getLastName());
        assertTrue(summary.isActive());
        assertSame(years, summary.getYears());
    }

    @Test
    void fullArgConstructor() {
        TrainerSummary summary = new TrainerSummary("anna.brown", "Anna", "Brown", false);
        assertEquals("anna.brown", summary.getUsername());
        assertEquals("Anna", summary.getFirstName());
        assertEquals("Brown", summary.getLastName());
        assertFalse(summary.isActive());
        assertNotNull(summary.getYears());
    }
}