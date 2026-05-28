package com.epam.workload.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainerSummaryResponseTest {

    @Test
    void noArgConstructor_andSetters() {
        TrainerSummaryResponse response = new TrainerSummaryResponse();
        response.setTrainerUsername("john.doe");
        response.setTrainerFirstName("John");
        response.setTrainerLastName("Doe");
        response.setTrainerStatus(true);
        Map<Integer, Map<Integer, Integer>> years = Map.of(2024, Map.of(3, 60));
        response.setYears(years);

        assertEquals("john.doe", response.getTrainerUsername());
        assertEquals("John", response.getTrainerFirstName());
        assertEquals("Doe", response.getTrainerLastName());
        assertTrue(response.isTrainerStatus());
        assertEquals(years, response.getYears());
    }

    @Test
    void fullArgConstructor() {
        Map<Integer, Map<Integer, Integer>> years = Map.of(2024, Map.of(6, 120));
        TrainerSummaryResponse response = new TrainerSummaryResponse(
                "anna.brown", "Anna", "Brown", false, years);

        assertEquals("anna.brown", response.getTrainerUsername());
        assertEquals("Anna", response.getTrainerFirstName());
        assertEquals("Brown", response.getTrainerLastName());
        assertFalse(response.isTrainerStatus());
        assertEquals(years, response.getYears());
    }
}