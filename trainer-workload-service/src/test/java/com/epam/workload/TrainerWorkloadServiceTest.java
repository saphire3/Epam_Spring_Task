package com.epam.workload;

import com.epam.workload.dto.ActionType;
import com.epam.workload.dto.TrainerSummaryResponse;
import com.epam.workload.dto.TrainerWorkloadRequest;
import com.epam.workload.model.MonthSummary;
import com.epam.workload.model.TrainerSummary;
import com.epam.workload.model.YearSummary;
import com.epam.workload.repository.TrainerSummaryRepository;
import com.epam.workload.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private TrainerSummaryRepository repository;

    @InjectMocks
    private TrainerWorkloadService service;

    private TrainerWorkloadRequest addRequest(String username, int year, int month, int duration) {
        return new TrainerWorkloadRequest(username, "John", "Doe", true,
                LocalDate.of(year, month, 15), duration, ActionType.ADD);
    }

    private TrainerWorkloadRequest deleteRequest(String username, int year, int month, int duration) {
        return new TrainerWorkloadRequest(username, "John", "Doe", true,
                LocalDate.of(year, month, 15), duration, ActionType.DELETE);
    }

    @Test
    void add_newTrainer_createsDocumentWithCorrectDuration() {
        when(repository.findByUsername("john.doe")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(addRequest("john.doe", 2024, 3, 60));

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(repository).save(captor.capture());

        TrainerSummary saved = captor.getValue();
        assertEquals("john.doe", saved.getUsername());
        assertEquals(1, saved.getYears().size());
        assertEquals(2024, saved.getYears().get(0).getYear());
        assertEquals(1, saved.getYears().get(0).getMonths().size());
        assertEquals(60, saved.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration());
    }

    @Test
    void add_existingTrainer_accumulatesMonthlyDuration() {
        TrainerSummary existing = trainerWithMonth("john.doe", 2024, 3, 60);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(addRequest("john.doe", 2024, 3, 90));

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(repository).save(captor.capture());

        int duration = captor.getValue().getYears().get(0).getMonths().get(0).getTrainingSummaryDuration();
        assertEquals(150, duration);
    }

    @Test
    void add_newYearForExistingTrainer_addsYearEntry() {
        TrainerSummary existing = trainerWithMonth("john.doe", 2023, 12, 100);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(addRequest("john.doe", 2024, 1, 45));

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(repository).save(captor.capture());

        assertEquals(2, captor.getValue().getYears().size());
    }

    @Test
    void delete_subtractsDuration() {
        TrainerSummary existing = trainerWithMonth("john.doe", 2024, 3, 90);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(deleteRequest("john.doe", 2024, 3, 30));

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(repository).save(captor.capture());

        int duration = captor.getValue().getYears().get(0).getMonths().get(0).getTrainingSummaryDuration();
        assertEquals(60, duration);
    }

    @Test
    void delete_doesNotGoBelowZero() {
        TrainerSummary existing = trainerWithMonth("john.doe", 2024, 3, 10);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.processWorkload(deleteRequest("john.doe", 2024, 3, 50));

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(repository).save(captor.capture());

        int duration = captor.getValue().getYears().get(0).getMonths().get(0).getTrainingSummaryDuration();
        assertEquals(0, duration);
    }

    @Test
    void getSummary_existingTrainer_returnsResponse() {
        TrainerSummary existing = trainerWithMonth("john.doe", 2024, 3, 60);
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        TrainerSummaryResponse response = service.getSummary("john.doe");

        assertNotNull(response);
        assertEquals("john.doe", response.getTrainerUsername());
        assertEquals(60, response.getYears().get(2024).get(3));
    }

    @Test
    void getSummary_unknownTrainer_returnsNull() {
        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertNull(service.getSummary("unknown"));
    }

    @Test
    void processWorkload_updatesTrainerProfileFields() {
        when(repository.findByUsername("john.doe")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john.doe", "Jane", "Smith", false,
                LocalDate.of(2024, 5, 1), 30, ActionType.ADD);
        service.processWorkload(request);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(repository).save(captor.capture());

        TrainerSummary saved = captor.getValue();
        assertEquals("Jane", saved.getFirstName());
        assertEquals("Smith", saved.getLastName());
        assertFalse(saved.isActive());
    }

    private TrainerSummary trainerWithMonth(String username, int year, int month, int duration) {
        TrainerSummary summary = new TrainerSummary(username, "John", "Doe", true);
        MonthSummary ms = new MonthSummary(month, duration);
        YearSummary ys = new YearSummary(year);
        ys.setMonths(new ArrayList<>(List.of(ms)));
        summary.setYears(new ArrayList<>(List.of(ys)));
        return summary;
    }
}