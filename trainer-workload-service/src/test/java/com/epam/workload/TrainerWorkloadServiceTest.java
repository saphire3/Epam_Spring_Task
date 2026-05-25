package com.epam.workload;

import com.epam.workload.dto.ActionType;
import com.epam.workload.dto.TrainerSummaryResponse;
import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainerWorkloadServiceTest {

    private TrainerWorkloadService service;

    @BeforeEach
    void setUp() {
        service = new TrainerWorkloadService();
    }

    @Test
    void addWorkload_accumulatesMonthlyDuration() {
        TrainerWorkloadRequest req = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2024, 3, 15), 60, ActionType.ADD);
        service.processWorkload(req);

        TrainerSummaryResponse summary = service.getSummary("john.doe");
        assertNotNull(summary);
        assertEquals(60, summary.getYears().get(2024).get(3));
    }

    @Test
    void addMultipleTrainings_sumsCorrectly() {
        TrainerWorkloadRequest req1 = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2024, 3, 15), 60, ActionType.ADD);
        TrainerWorkloadRequest req2 = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2024, 3, 20), 90, ActionType.ADD);
        service.processWorkload(req1);
        service.processWorkload(req2);

        TrainerSummaryResponse summary = service.getSummary("john.doe");
        assertEquals(150, summary.getYears().get(2024).get(3));
    }

    @Test
    void deleteWorkload_subtractsDuration() {
        TrainerWorkloadRequest add = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2024, 3, 15), 90, ActionType.ADD);
        TrainerWorkloadRequest delete = new TrainerWorkloadRequest(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2024, 3, 15), 30, ActionType.DELETE);
        service.processWorkload(add);
        service.processWorkload(delete);

        TrainerSummaryResponse summary = service.getSummary("john.doe");
        assertEquals(60, summary.getYears().get(2024).get(3));
    }

    @Test
    void getSummary_unknownTrainer_returnsNull() {
        assertNull(service.getSummary("unknown.trainer"));
    }
}
